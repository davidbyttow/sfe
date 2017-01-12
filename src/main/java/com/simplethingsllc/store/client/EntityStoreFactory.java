package com.simplethingsllc.store.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.util.Providers;
import com.simplethingsllc.store.client.config.ClientConfig;
import com.simplethingsllc.store.client.config.CompositeIndexDef;
import com.simplethingsllc.store.server.EntitiesStorage;
import com.simplethingsllc.store.server.EntityMetadata;
import com.simplethingsllc.store.server.EntityMetadataService;
import com.simplethingsllc.store.server.EntityPropertiesGenerator;
import com.simplethingsllc.store.server.EntityStoreImpl;
import com.simplethingsllc.store.server.MetadataManager;
import com.simplethingsllc.store.server.driver.SqlExecutor;
import com.simplethingsllc.store.server.driver.StoreInitializer;
import com.simplethingsllc.store.server.index.CompositeIndexes;
import com.simplethingsllc.store.server.index.IndexGenerator;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public final class EntityStoreFactory {
  public static EntityStoreClient createClient(ClientConfig config) {
    DBI dbi = Preconditions.checkNotNull(config.dbi);
    ExecutorService executorService = Preconditions.checkNotNull(config.executorService);
    ObjectMapper objectMapper = Preconditions.checkNotNull(config.objectMapper);
    DataSource dataSource = Preconditions.checkNotNull(config.dataSource);

    if (!config.skipMigrations) {
      StoreInitializer.runMigrations(dataSource);
    }

    ListMultimap<String, CompositeIndexDef> indexMap = CompositeIndexes.loadIndexes(config.indexes);
    MetadataManager metadataManager = new MetadataManager(indexMap);
    for (Class<?> type : config.entityTypes) {
      metadataManager.registerType(type);
    }

    EntitiesStorage storage = dbi.onDemand(EntitiesStorage.class);
    SqlExecutor sqlExecutor = new SqlExecutor(Providers.of(dbi));

    Map<Class<?>, EntityMetadata> metadataMap = metadataManager.getMetadataClassMap();
    EntityMetadataService metadataService = new EntityMetadataService(
      new EntityPropertiesGenerator(sqlExecutor),
      new IndexGenerator(sqlExecutor),
      metadataMap);
    metadataService.updateAll();

    EntityStoreImpl impl = new EntityStoreImpl(
      storage, sqlExecutor, objectMapper, metadataMap, MoreExecutors.listeningDecorator(executorService));
    return new EntityStoreClient(impl);
  }

  private EntityStoreFactory() {}
}
