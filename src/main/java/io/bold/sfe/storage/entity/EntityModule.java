package io.bold.sfe.storage.entity;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.environment.ClassLoaders;
import io.bold.sfe.inject.LazySingleton;
import io.bold.sfe.storage.entity.index.CompositeIndexDef;
import io.bold.sfe.storage.entity.index.CompositeIndexes;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public final class EntityModule extends AbstractModule {

  private final String packagePrefix;
  private final String indexFilePath;

  public EntityModule(String packagePrefix, String indexFilePath) {
    this.packagePrefix = packagePrefix;
    this.indexFilePath = indexFilePath;
  }

  @Override protected void configure() {}

  @Provides @LazySingleton
  public ListMultimap<String, CompositeIndexDef> compositeIndexMap() throws IOException {
    InputStream stream = ClassLoaders.open(indexFilePath);
    if (stream == null) {
      return ArrayListMultimap.create();
    }
    String json = IOUtils.toString(stream);
    return CompositeIndexes.loadIndexesFromJson(json);
  }

  @Provides @Singleton
  List<EntityMetadata> entityMetadataList(Map<Class<?>, EntityMetadata> metadataMap) {
    return ImmutableList.copyOf(metadataMap.values());
  }

  @Provides @Singleton
  public Map<Class<?>, EntityMetadata> entityMetadataMapByClass(
      ListMultimap<String, CompositeIndexDef> compositeIndexDefMap) {
    ImmutableMap.Builder<Class<?>, EntityMetadata> builder = ImmutableMap.builder();
    for (Class<?> entityClass : MoreReflections.getTypesAnnotatedWith(packagePrefix, EntityKind.class)) {
      EntityKind entityKind = entityClass.getAnnotation(EntityKind.class);
      List<CompositeIndexDef> indexes = compositeIndexDefMap.get(entityKind.value());
      builder.put(entityClass, EntityMetadata.fromType(entityClass, indexes));
    }
    return builder.build();
  }

  @Provides @Singleton
  public Map<String, EntityMetadata> entityMetadataMapByKind(List<EntityMetadata > metadataList) {
    ImmutableMap.Builder<String, EntityMetadata> builder = ImmutableMap.builder();
    for (EntityMetadata metadata : metadataList) {
      builder.put(metadata.getKind(), metadata);
    }
    return builder.build();
  }
}
