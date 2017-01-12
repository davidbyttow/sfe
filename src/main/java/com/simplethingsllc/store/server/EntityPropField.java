package com.simplethingsllc.store.server;

import com.simplethingsllc.store.client.IdType;
import com.simplethingsllc.store.common.Fields;

import java.lang.reflect.Field;

public class EntityPropField {
  private final Prop.Type type;
  private final Field field;
  private Prop.Type elementType;

  public static EntityPropField fromField(Field field, boolean indexed) {
    Class<?> type = field.getType();
    Prop.Type propType = null;
    Prop.Type elementType = null;

    Class<?> innerType = Fields.getCollectionType(field);
    if (innerType != null) {
      // We only use List type when the object is indexed, otherwise it's just a plain old object.
      Prop.Type innerPropType = PropTypes.fromType(innerType);
      if (innerPropType != null && indexed) {
        propType = Prop.Type.List;
        elementType = innerPropType;
      }
    } else {
      propType = PropTypes.fromType(type);
    }

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

    EntityPropField f = new EntityPropField(propType, field);
    f.elementType = elementType;
    return f;
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

  public Prop.Type getElementType() {
    return (elementType == null) ? type : elementType;
  }
}
