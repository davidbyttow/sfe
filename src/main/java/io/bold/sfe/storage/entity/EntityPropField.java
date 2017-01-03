package io.bold.sfe.storage.entity;

import io.bold.sfe.common.MoreReflections;

import java.lang.reflect.Field;

public class EntityPropField {
  private final Prop.Type type;
  private final Field field;
  private boolean isCollection;

  public static EntityPropField fromField(Field field, boolean indexed) {
    Class<?> type = MoreReflections.getFieldCollectionType(field);
    boolean isCollection = type != null;

    if (type == null) {
      type = field.getType();
    }

    Prop.Type propType = PropTypes.fromType(type);
    if (propType == null) {
      if (indexed) {
        throw new IllegalArgumentException("Invalid prop type for indexing: " + type);
      }
      propType = Prop.Type.Object;
    }

    if (field.isAnnotationPresent(IdType.class)) {
      if (indexed && propType == Prop.Type.String) {
        propType = Prop.Type.Id;
      } else {
        throw new IllegalArgumentException("Secondary ids are only available for indexed String types");
      }
    }

    EntityPropField propField = new EntityPropField(propType, field);
    propField.isCollection = isCollection;
    return propField;
  }

  private EntityPropField(Prop.Type type, Field field) {
    this.type = type;
    this.field = field;
  }

  public String getName() {
    return field.getName();
  }

  public Prop.Type getType() {
    return type;
  }

  public Field getField() {
    return field;
  }

  public boolean isCollection() {
    return isCollection;
  }
}
