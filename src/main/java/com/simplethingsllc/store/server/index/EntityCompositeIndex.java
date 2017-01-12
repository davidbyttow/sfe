package com.simplethingsllc.store.server.index;

import com.simplethingsllc.store.server.EntityPropField;

import java.util.List;
import java.util.stream.Collectors;

public class EntityCompositeIndex {
  private final List<EntityPropField> fields;
  private final List<String> fieldNames;

  public EntityCompositeIndex(List<EntityPropField> fields) {
    this.fields = fields;
    this.fieldNames = fields.stream().map(EntityPropField::getName).collect(Collectors.toList());
  }

  public List<EntityPropField> getFields() {
    return fields;
  }

  public List<String> getFieldNames() {
    return fieldNames;
  }
}
