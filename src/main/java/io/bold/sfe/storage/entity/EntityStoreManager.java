package io.bold.sfe.storage.entity;

import com.google.common.collect.ListMultimap;
import io.bold.sfe.storage.entity.index.CompositeIndexDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityStoreManager {
  private final List<EntityMetadata> metadataList = new ArrayList<>();
  private final Map<Class<?>, EntityMetadata> classMap = new HashMap<>();
  private final Map<String, EntityMetadata> kindMap = new HashMap<>();
  private final ListMultimap<String, CompositeIndexDef> compositeIndexDefMap;

  public EntityStoreManager(ListMultimap<String, CompositeIndexDef> compositeIndexDefMap) {
    this.compositeIndexDefMap = compositeIndexDefMap;
  }

  public void registerType(Class<?> entityClass) {
    EntityKind entityKind = entityClass.getAnnotation(EntityKind.class);
    if (entityKind == null) {
      throw new IllegalArgumentException("Type not annotated with @EntityKind");
    }
    List<CompositeIndexDef> indexes = compositeIndexDefMap.get(entityKind.value());
    EntityMetadata metadata = EntityMetadata.fromType(entityClass, indexes);
    classMap.put(entityClass, metadata);
    kindMap.put(entityKind.value(), metadata);
    metadataList.add(metadata);
  }

  public List<EntityMetadata> getMetadataList() {
    return metadataList;
  }

  public Map<Class<?>, EntityMetadata> getMetadataClassMap() {
    return classMap;
  }

  public Map<String, EntityMetadata> getMetadataKindMap() {
    return kindMap;
  }
}
