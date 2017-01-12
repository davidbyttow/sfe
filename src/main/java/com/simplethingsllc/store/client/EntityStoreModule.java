package com.simplethingsllc.store.client;

import com.google.common.base.Charsets;
import com.google.common.collect.ListMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.simplethingsllc.store.client.config.CompositeIndexDef;
import com.simplethingsllc.store.server.EntityMetadata;
import com.simplethingsllc.store.server.MetadataManager;
import com.simplethingsllc.store.server.index.CompositeIndexes;
import io.bold.sfe.environment.ClassLoaders;
import io.bold.sfe.inject.LazySingleton;
import io.bold.sfe.json.Json;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class EntityStoreModule extends AbstractModule {

  private final String indexFilePath;
  private final Set<Class<?>> entityClasses;

  public EntityStoreModule(String indexFilePath, Set<Class<?>> entityClasses) {
    this.indexFilePath = indexFilePath;
    this.entityClasses = entityClasses;
  }

  @Override protected void configure() {}

  @Provides
  List<EntityMetadata> entityMetadataList(MetadataManager metadataManager) {
    return metadataManager.getMetadataList();
  }

  @Provides
  public Map<Class<?>, EntityMetadata> entityMetadataMapByClass(MetadataManager metadataManager) {
    return metadataManager.getMetadataClassMap();
  }

  @Provides
  public Map<String, EntityMetadata> entityMetadataMapByKind(MetadataManager metadataManager) {
    return metadataManager.getMetadataKindMap();
  }

  @Provides @LazySingleton
  public MetadataManager entityStoreManager(ListMultimap<String, CompositeIndexDef> compositeIndexMap) {
    MetadataManager metadataManager = new MetadataManager(compositeIndexMap);
    for (Class<?> c : entityClasses) {
      metadataManager.registerType(c);
    }
    return metadataManager;
  }

  public static class IndexesDef {
    List<CompositeIndexDef> indexes = new ArrayList<>();
  }

  public static List<CompositeIndexDef> loadIndexesFromJson(String indexFilePath) throws IOException {
    InputStream stream = ClassLoaders.open(indexFilePath);
    if (stream == null) {
      return new ArrayList<>();
    }
    String json = IOUtils.toString(stream, Charsets.UTF_8);
    return Json.readValue(json, IndexesDef.class).indexes;
  }

  @Provides @LazySingleton
  public ListMultimap<String, CompositeIndexDef> compositeIndexMap() throws IOException {
    return CompositeIndexes.loadIndexes(loadIndexesFromJson(indexFilePath));
  }
}
