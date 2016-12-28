package com.davidbyttow.sfe.storage.entity;

import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class EntityMetadataData {
  static class IndexedProperty {
    @NotNull Prop.Type type;
    @NotNull @NotEmpty String name;
  }

  private EntityMetadataData() {}

  private List<IndexedProperty> indexedProperties = new ArrayList<>();

  public List<IndexedProperty> getIndexedProperties() {
    return indexedProperties;
  }

  static EntityMetadataData fromMetadata(EntityMetadata metadata) {
    EntityMetadataData d = new EntityMetadataData();
    d.indexedProperties = Lists.transform(metadata.getIndexedFields(), m -> {
      IndexedProperty p = new IndexedProperty();
      p.name = m.getName();
      p.type = m.getType();
      return p;
    });
    return d;
  }
}
