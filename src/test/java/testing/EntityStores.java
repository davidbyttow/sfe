package com.davidbyttow.sfe.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ListMultimap;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.util.Providers;
import com.davidbyttow.sfe.json.Json;
import com.davidbyttow.sfe.storage.entity.EntitiesStorage;
import com.davidbyttow.sfe.storage.entity.EntityMetadata;
import com.davidbyttow.sfe.storage.entity.EntityMetadataService;
import com.davidbyttow.sfe.storage.entity.EntityModule;
import com.davidbyttow.sfe.storage.entity.EntityPropertiesGenerator;
import com.davidbyttow.sfe.storage.entity.EntityStore;
import com.davidbyttow.sfe.storage.entity.SqlExecutor;
import com.davidbyttow.sfe.storage.entity.index.CompositeIndexDef;
import com.davidbyttow.sfe.storage.entity.index.IndexGenerator;
import org.skife.jdbi.v2.DBI;

import java.io.IOException;
import java.util.Map;

public final class EntityStores {
  public static EntityStore createEntityStore(DBI dbi) throws IOException {
    EntityModule module = new EntityModule("io.bold", "/test/db/indexes.json");
    ListMultimap<String, CompositeIndexDef> indexMap =
        module.compositeIndexMap();
    Map<Class<?>, EntityMetadata> metadataMap = module.entityMetadataMapByClass(indexMap);
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
