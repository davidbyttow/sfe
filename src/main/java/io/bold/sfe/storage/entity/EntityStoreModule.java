package io.bold.sfe.storage.entity;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.bold.sfe.environment.ClassLoaders;
import io.bold.sfe.inject.LazySingleton;
import io.bold.sfe.storage.entity.index.CompositeIndexDef;
import io.bold.sfe.storage.entity.index.CompositeIndexes;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
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
  List<EntityMetadata> entityMetadataList(EntityStoreManager entityStoreManager) {
    return entityStoreManager.getMetadataList();
  }

  @Provides
  public Map<Class<?>, EntityMetadata> entityMetadataMapByClass(EntityStoreManager entityStoreManager) {
    return entityStoreManager.getMetadataClassMap();
  }

  @Provides
  public Map<String, EntityMetadata> entityMetadataMapByKind(EntityStoreManager entityStoreManager) {
    return entityStoreManager.getMetadataKindMap();
  }

  @Provides @LazySingleton
  public EntityStoreManager entityStoreManager(ListMultimap<String, CompositeIndexDef> compositeIndexMap) {
    EntityStoreManager entityStoreManager = new EntityStoreManager(compositeIndexMap);
    for (Class<?> c : entityClasses) {
      entityStoreManager.registerType(c);
    }
    return entityStoreManager;
  }

  @Provides @LazySingleton
  public ListMultimap<String, CompositeIndexDef> compositeIndexMap() throws IOException {
    InputStream stream = ClassLoaders.open(indexFilePath);
    if (stream == null) {
      return ArrayListMultimap.create();
    }
    String json = IOUtils.toString(stream);
    return CompositeIndexes.loadIndexesFromJson(json);
  }
}
