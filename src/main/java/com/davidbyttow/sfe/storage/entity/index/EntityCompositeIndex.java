package com.davidbyttow.sfe.storage.entity.index;

import com.davidbyttow.sfe.common.Streams;
import com.davidbyttow.sfe.storage.entity.EntityPropField;

import java.util.List;

public class EntityCompositeIndex {
  private final List<EntityPropField> fields;
  private final List<String> fieldNames;

  public EntityCompositeIndex(List<EntityPropField> fields) {
    this.fields = fields;
    this.fieldNames = Streams.transform(fields, EntityPropField::getName);
  }

  public List<EntityPropField> getFields() {
    return fields;
  }

  public List<String> getFieldNames() {
    return fieldNames;
  }
}
