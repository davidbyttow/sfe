package com.davidbyttow.sfe.storage.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.google.inject.Inject;
import com.davidbyttow.sfe.common.MutableInt;
import com.davidbyttow.sfe.common.Times;
import com.davidbyttow.sfe.concurrent.BackgroundThreadPool;
import com.davidbyttow.sfe.storage.ForWrites;
import com.davidbyttow.sfe.storage.entity.index.CompositeIndexes;
import com.davidbyttow.sfe.storage.entity.index.EntityCompositeIndex;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EntityStore implements EntityStoreAsync {

  private static final Logger log = LoggerFactory.getLogger(EntityStore.class);

  private final EntitiesStorage entitiesStorage;
  private final SqlExecutor sqlExecutor;
  private final ObjectMapper objectMapper;
  private final Map<Class<?>, EntityMetadata> metadataMap;
  private final ListeningExecutorService executor;

  @Inject public EntityStore(@ForWrites EntitiesStorage entitiesStorage,
                             SqlExecutor sqlExecutor,
                             ObjectMapper objectMapper,
                             Map<Class<?>, EntityMetadata> metadataMap,
                             @BackgroundThreadPool ListeningExecutorService executor) {
    this.entitiesStorage = entitiesStorage;
    this.sqlExecutor = sqlExecutor;
    this.objectMapper = objectMapper;
    this.metadataMap = metadataMap;
    this.executor = executor;
  }

  public void put(Object entity) {
    Futures.getUnchecked(putAsync(entity));
  }

  public void putMany(List<?> entities) {
    Futures.getUnchecked(putManyAsync(entities));
  }

  @Nullable public <T> T get(String id, Class<T> type) {
    return Futures.getUnchecked(getAsync(id, type));
  }

  public <T> List<T> getMany(List<String> ids, Class<T> type) {
    return Futures.getUnchecked(getManyAsync(ids, type));
  }

  public String getKind(Object entity) {
    EntityMetadata metadata = getMetadata(entity);
    return metadata.getKind();
  }

  public String getId(Object entity) {
    EntityMetadata metadata = getMetadata(entity);
    return getId(entity, metadata);
  }

  @Override public ListenableFuture<Void> putAsync(Object entity) {
    EntityMetadata metadata = getMetadata(entity);
    String id = getId(entity, metadata);

    DateTime now = Times.nowUtc();

    return executor.submit(() -> {
      saveEntity(entity, id, metadata, now);

      // Don't block on indexes.
      executor.submit(() -> {
        saveIndexedProps(entity, id, metadata, now);
        updateCompositeIndexes(entity, id, metadata, now);
      });

      return null;
    });
  }

  @Override public ListenableFuture<Void> putManyAsync(List<?> entities) {
    List<ListenableFuture<Void>> futures = new ArrayList<>(entities.size());
    for (Object entity : entities) {
      futures.add(putAsync(entity));
    }
    return waitForAll(Futures.allAsList(futures));
  }

  @Override public <T> ListenableFuture<T> getAsync(String id, Class<T> type) {
    return executor.submit(() -> {
      EntityMetadata metadata = fromType(type);
      return readEntity(metadata, id, type);
    });
  }

  @Override public <T> ListenableFuture<List<T>> getManyAsync(List<String> ids, Class<T> type) {
    List<ListenableFuture<T>> futures = new ArrayList<>(ids.size());
    for (String id : ids) {
      futures.add(getAsync(id, type));
    }
    return Futures.allAsList(futures);
  }

  public void delete(Object entity) {
    Futures.getUnchecked(deleteAsync(entity));
  }

  public void delete(String id, Class<?> type) {
    Futures.getUnchecked(deleteAsync(id, type));
  }

  @Override public ListenableFuture<Void> deleteAsync(Object entity) {
    return executor.submit(() -> {
      EntityMetadata metadata = getMetadata(entity);
      String id = getId(entity, metadata);
      delete(id, entity.getClass());
      return null;
    });
  }

  @Override public ListenableFuture<Void> deleteAsync(String id, Class<?> type) {
    return executor.submit(() -> {
      EntityMetadata metadata = fromType(type);
      entitiesStorage.delete(metadata.getKind(), id);

      executor.submit(() -> {
        String sql = String.format("DELETE FROM %s_properties WHERE entity_id = '%s'", metadata.getKind(), id);
        sqlExecutor.update(sql);

        // Delete composite index entries.
        deleteCompositeIndexes(metadata, id);
      });

      return null;
    });
  }

  public void deleteMany(List<String> ids, Class<?> type) {
    Futures.getUnchecked(deleteManyAsync(ids, type));
  }

  @Override public ListenableFuture<Void> deleteManyAsync(List<String> ids, Class<?> type) {
    List<ListenableFuture<Void>> futures = new ArrayList<>(ids.size());
    for (String id : ids) {
      futures.add(deleteAsync(id, type));
    }
    return waitForAll(Futures.allAsList(futures));
  }

  public int count(Query<?> query) {
    return Futures.getUnchecked(countAsync(query));
  }

  @Override public ListenableFuture<Integer> countAsync(Query<?> query) {
    return executor.submit(() -> {
      // TODO(d): Don't read entities just to count.
      MutableInt count = MutableInt.of(0);
      forEachEntity(query, entity -> count.increment(1));
      return count.get();
    });
  }

  public Class<?> findEntityType(String kind) {
    for (Map.Entry<Class<?>, EntityMetadata> entry : metadataMap.entrySet()) {
      if (entry.getValue().getKind().equals(kind)) {
        return entry.getKey();
      }
    }
    return null;
  }

  private ListenableFuture<Void> waitForAll(ListenableFuture<List<Void>> list) {
    SettableFuture<Void> settable = SettableFuture.create();
    list.addListener(() -> settable.set(null), executor);
    return settable;
  }

  // TODO(d): Remove this. It's here because the frontend can't inject the map itself in a handler
  // for some odd reason.
  public ImmutableMap<Class<?>, EntityMetadata> getEntityMetadataMap() {
    return ImmutableMap.copyOf(metadataMap);
  }

  private EntityMetadata getMetadata(Object entity) {
    Class<?> type = entity.getClass();
    EntityMetadata metadata = fromType(type);
    if (metadata == null) {
      throw new IllegalArgumentException("No metadata found for object " + entity);
    }
    return metadata;
  }

  private String getId(Object entity, EntityMetadata metadata) {
    String id = metadata.getIdRef().getId(entity);
    if (Strings.isNullOrEmpty(id)) {
      throw new IllegalArgumentException("Missing id for entity");
    }
    return id;
  }

  private void saveEntity(Object entity, String id, EntityMetadata metadata, DateTime now) {
    EntityHandler<Object> handler = (EntityHandler<Object>) metadata.getHandler();
    entity = handler.onBeforeSave(entity);

    byte[] jsonData;
    try {
      jsonData = objectMapper.writeValueAsBytes(entity);
    } catch (JsonProcessingException e) {
      throw Throwables.propagate(e);
    }

    byte[] jsonMetadata;
    try {
      EntityMetadataData metadataData = EntityMetadataData.fromMetadata(metadata);
      jsonMetadata = objectMapper.writeValueAsBytes(metadataData);
    } catch (JsonProcessingException e) {
      throw Throwables.propagate(e);
    }

    EntityData entityData = new EntityData();
    entityData.setId(id);
    entityData.setKind(metadata.getKind());
    entityData.setJsonData(jsonData);
    entityData.setJsonMetadata(jsonMetadata);
    entityData.setCreatedAt(now);
    entityData.setUpdatedAt(now);
    entitiesStorage.create(entityData);
  }

  private void saveIndexedProps(Object entity, String id, EntityMetadata metadata, DateTime now) {
    if (metadata.getIndexedFields().isEmpty()) {
      return;
    }

    UpdateGenerator generator = new UpdateGenerator(metadata);
    String sql = generator.updateIndexedProperties(entity, id, now);
    sqlExecutor.update(sql);
  }

  private void deleteCompositeIndexes(EntityMetadata metadata, String id) {
    List<EntityCompositeIndex> indexes = metadata.getCompositeIndexes();
    if (indexes.isEmpty()) {
      return;
    }

    for (EntityCompositeIndex index : indexes) {
      String sql = String.format("DELETE FROM %s WHERE entity_id = '%s'",
          CompositeIndexes.getIndexTableName(metadata.getKind(), index.getFieldNames()), id);
      sqlExecutor.update(sql);
    }
  }

  public int backfillKind(Class<?> type) {
    EntityMetadata metadata = metadataMap.get(type);
    if (metadata == null) {
      return 0;
    }
    DateTime now = Times.nowUtc();
    MutableInt count = MutableInt.of(0);
    for (EntityCompositeIndex index : metadata.getCompositeIndexes()) {
      String tableName = CompositeIndexes.getIndexTableName(metadata.getKind(), index.getFieldNames());
      String sql = String.format("SELECT id FROM entities WHERE kind='%s' AND id NOT IN (SELECT entity_id FROM %s) ORDER BY updated_at",
        metadata.getKind(), tableName);
      sqlExecutor.query(sql, row -> {
        String entityId = (String) row.get("id");
        Object entity = readEntity(metadata, entityId, metadata.getType());
        if (entity != null) {
          updateCompositeIndexes(entity, entityId, metadata, now);
          count.increment(1);
        }
      });
    }
    return count.get();
  }

  private void updateCompositeIndexes(Object entity, String id, EntityMetadata metadata, DateTime now) {
    List<EntityCompositeIndex> indexes = metadata.getCompositeIndexes();
    if (indexes.isEmpty()) {
      return;
    }

    UpdateGenerator generator = new UpdateGenerator(metadata);
    for (EntityCompositeIndex index : indexes) {
      String sql = generator.updateCompositeIndex(entity, id, index, now);
      sqlExecutor.update(sql);
    }
  }

  private <T> T readEntity(EntityMetadata metadata, String id, Class<T> type) {
    EntityData entityData = entitiesStorage.read(metadata.getKind(), id);
    if (entityData == null) {
      return null;
    }
    if (!entityData.getId().equals(id)) {
      throw new IllegalStateException("Id mismatch on object");
    }

    return toEntity(metadata, entityData, type);
  }

  private <T> T toEntity(EntityMetadata metadata, EntityData entityData, Class<T> type) {
    byte[] jsonData = entityData.getJsonData();

    T entity;
    try {
      entity = objectMapper.readValue(jsonData, type);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    metadata.getIdRef().setId(entity, entityData.getId());
    return entity;
  }

  @Override public <T> ListenableFuture<List<T>> fetchAsync(Query<T> query) {
    // TODO(d): Turn this into a streaming query.
    return executor.submit(() -> fetch(query));
  }

  public <T> List<T> fetch(Query<T> query) {
    List<T> entities = new ArrayList<>();
    forEachEntity(query, entities::add);
    return entities;
  }

  public <T> PageResults<T> fetchPage(Query<T> query) {
    List<T> entities = new ArrayList<>();
    PageResults<T> page = new PageResults<>(query);
    page.endCursor = "";
    return fetchNext(page);
  }

  // TODO(d): This API sucks but figure it out later.
  public <T> PageResults<T> fetchNext(PageResults<T> page) {
    EntityMetadata metadata = fromType(page.query.getType());
    List<Query.Filter> filters = page.query.getFilters();
    Query.Ordering ordering = page.query.getOrdering();

    PageResults<T> results = new PageResults<>(page.query);
    if (filters.isEmpty() && ordering == null) {
      // Fetch one more than extra so we know if there's more results.
      List<EntityData> data;
      if (page.hasCursor()) {
        data = entitiesStorage.scanAfter(metadata.getKind(), page.endCursor, page.query.getLimit() + 1);
      } else {
        data = entitiesStorage.scan(metadata.getKind(), page.query.getLimit() + 1);
      }
      if (data.size() > page.query.getLimit()) {
        data.remove(data.size() - 1);
        results.endCursor = data.get(data.size() - 1).getId();
      }
      data.forEach((entityData) -> {
        results.entities.add(toEntity(metadata, entityData, page.query.getType()));
      });
    } else {
      throw new IllegalArgumentException("Ordering and filters not yet available for pagination");
    }
    return results;
  }

  private static boolean hasUniqueProperty(List<Query.Filter> filters) {
    if (filters.isEmpty()) {
      return false;
    }
    String prop = filters.get(0).getProperty();
    for (Query.Filter f : filters) {
      if (!f.getProperty().equals(prop)) {
        return false;
      }
    }
    return true;
  }


  private <T> void forEachEntity(Query<T> query, Consumer<T> consumer) {
    EntityMetadata metadata = fromType(query.getType());
    List<Query.Filter> filters = query.getFilters();
    Query.Ordering ordering = query.getOrdering();
    QueryGenerator queryGen = new QueryGenerator(metadata);

    String sql;
    if (filters.isEmpty() && ordering == null) {
      List<EntityData> entities = entitiesStorage.scan(metadata.getKind(), query.getLimit());
      entities.forEach((entityData) -> {
        consumer.accept(toEntity(metadata, entityData, query.getType()));
      });
      return;
    }

    if (hasUniqueProperty(filters) && ordering == null) {
      sql = queryGen.singleFilter(filters, query.getLimit());
    } else if (filters.isEmpty() && ordering != null) {
      sql = queryGen.orderedBy(ordering, query.getLimit());
    } else if(filters.size() == 1 && ordering != null && filters.get(0).getProperty().equals(ordering.getProperty())) {
      sql = queryGen.singleFilterOrdered(filters.get(0), ordering.isDescending(), query.getLimit());
    } else {
      // Search for a composite index.
      EntityCompositeIndex index = CompositeIndexes.findCompositeIndex(metadata, query.getProperties());
      if (index == null) {
        throw new IllegalArgumentException("Composite index not found for properties " + query.getProperties());
      }
      if (ordering == null) {
        sql = queryGen.multiFilter(filters, query.getLimit());
      } else {
        sql = queryGen.multiFilterOrdered(filters, ordering, query.getLimit());
      }
    }

    List<String> resultIds = new ArrayList<>();
    Multimap<String, EntityPropField> entityPropFields = ArrayListMultimap.create();

    Map<String, EntityPropField> indexedFieldMap = metadata.getIndexedFieldMap();

    // TODO(d): Shorten this?
    sqlExecutor.query(sql, row -> {
      String entityId = (String) row.get("entity_id");
      String propKey = (String) row.get("prop_key");
      resultIds.add(entityId);
      EntityPropField propField = indexedFieldMap.get(propKey);
      entityPropFields.put(entityId, propField);
    });

    // TODO(d): Make this more efficient.
    resultIds.stream()
      .map((entityId) -> readEntity(metadata, entityId, query.getType()))
      .forEach((entity) -> {
        if (entity != null) {
          consumer.accept(entity);
        }
      });
  }

  private EntityMetadata fromType(Class<?> type) {
    EntityMetadata metadata = metadataMap.get(type);
    if (metadata == null) {
      throw new IllegalArgumentException("Unknown entity type: " + type);
    }
    return metadata;
  }
}
