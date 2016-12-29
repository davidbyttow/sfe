package io.bold.sfe.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ListMultimap;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.util.Providers;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.json.Json;
import io.bold.sfe.storage.entity.EntitiesStorage;
import io.bold.sfe.storage.entity.EntityKind;
import io.bold.sfe.storage.entity.EntityMetadata;
import io.bold.sfe.storage.entity.EntityMetadataService;
import io.bold.sfe.storage.entity.EntityPropertiesGenerator;
import io.bold.sfe.storage.entity.EntityStore;
import io.bold.sfe.storage.entity.EntityStoreManager;
import io.bold.sfe.storage.entity.EntityStoreModule;
import io.bold.sfe.storage.entity.SqlExecutor;
import io.bold.sfe.storage.entity.index.CompositeIndexDef;
import io.bold.sfe.storage.entity.index.IndexGenerator;
import org.skife.jdbi.v2.DBI;

import java.io.IOException;
import java.util.Map;

public final class EntityStores {
  public static EntityStore createEntityStore(DBI dbi) throws IOException {
    EntityStoreModule module = new EntityStoreModule(
      "/test/db/indexes.json", MoreReflections.getTypesAnnotatedWith("io.bold.sfe", EntityKind.class));
    ListMultimap<String, CompositeIndexDef> indexMap = module.compositeIndexMap();
    EntityStoreManager manager = module.entityStoreManager(indexMap);
    Map<Class<?>, EntityMetadata> metadataMap = manager.getMetadataClassMap();
    EntitiesStorage es = dbi.onDemand(EntitiesStorage.class);
    ObjectMapper objectMapper = Json.newObjectMapper();

    SqlExecutor sqlExecutor = new SqlExecutor(Providers.of(dbi));

    EntityMetadataService metadataService = new EntityMetadataService(
        new EntityPropertiesGenerator(sqlExecutor),
        new IndexGenerator(sqlExecutor),
        metadataMap);
    metadataService.updateAll();
    return new EntityStore(es, sqlExecutor, objectMapper, metadataMap, MoreExecutors.listeningDecorator(new ImmediateExecutor()));
  }
}
