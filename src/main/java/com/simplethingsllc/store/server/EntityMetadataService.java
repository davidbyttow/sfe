package com.simplethingsllc.store.server;

import com.google.inject.Inject;
import com.simplethingsllc.store.server.index.EntityPropertiesGenerator;
import com.simplethingsllc.store.server.index.IndexGenerator;

import java.util.Map;

public class EntityMetadataService {

  private final EntityPropertiesGenerator propertiesGenerator;
  private final IndexGenerator indexGenerator;
  private final Map<Class<?>, EntityMetadata> metadataMap;

  @Inject public EntityMetadataService(EntityPropertiesGenerator propertiesGenerator,
                                       IndexGenerator indexGenerator,
                                       Map<Class<?>, EntityMetadata> metadataMap) {
    this.propertiesGenerator = propertiesGenerator;
    this.indexGenerator = indexGenerator;
    this.metadataMap = metadataMap;
  }

  public void updateAll() {
    for (EntityMetadata metadata : metadataMap.values()) {
      propertiesGenerator.maybeGenerateProperties(metadata);
      indexGenerator.maybeGenerateIndexes(metadata);
    }
  }
}
