package com.davidbyttow.sfe.storage.entity.index;

import java.util.List;

public class CompositeIndexDef {
  private final String kind;
  private final List<String> propertyNames;

  CompositeIndexDef(String kind, List<String> propertyNames) {
    this.kind = kind;
    this.propertyNames = propertyNames;
  }

  public String getKind() {
    return kind;
  }

  public List<String> getPropertyNames() {
    return propertyNames;
  }
}
