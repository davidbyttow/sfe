package com.simplethingsllc.store.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public final class Fields {

  public static Class<?> getCollectionType(Field field) {
    Class<?> type = field.getType();
    if (type.isArray()) {
      return type.getComponentType();
    }

    if (Collection.class.isAssignableFrom(type)) {
      ParameterizedType pt = (ParameterizedType) field.getGenericType();
      Type[] pts = pt.getActualTypeArguments();
      if (pts.length == 1) {
        return (Class<?>)pts[0];
      }
    }
    return null;
  }

  private Fields() {}
}
