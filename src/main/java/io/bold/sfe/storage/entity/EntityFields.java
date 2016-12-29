package io.bold.sfe.storage.entity;

import com.google.common.base.Throwables;
import org.joda.time.DateTime;

import java.lang.reflect.Field;

public final class EntityFields {

  @FunctionalInterface
  interface FieldGetter<T> {
    T get(Object obj, Field field) throws IllegalAccessException;
  }

  public static <T> T getValue(Object obj, Field field, FieldGetter<T> getter) {
    boolean unset = makeAccessibleIfNeeded(field);
    try {
      return getter.get(obj, field);
    } catch (IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T getValue(Object obj, Field field) {
    return getValue(obj, field, (o, f) -> (T) f.get(o));
  }

  @SuppressWarnings("unchecked")
  public static <T> T getValueOrDefault(Object obj, Field field, T def) {
    T value = getValue(obj, field, (o, f) -> (T) f.get(o));
    return (value == null) ? def : value;
  }

  public static boolean getBoolean(Object obj, Field field) {
    return getValue(obj, field, (o, f) -> f.getBoolean(o));
  }

  public static int getInteger(Object obj, Field field) {
    return getValue(obj, field, (o, f) -> f.getInt(o));
  }

  public static long getLong(Object obj, Field field) {
    return getValue(obj, field, (o, f) -> f.getLong(o));
  }

  public static float getFloat(Object obj, Field field) {
    return getValue(obj, field, (o, f) -> f.getFloat(o));
  }

  public static String getEnumName(Object obj, Field field) {
    return getValue(obj, field, (o, f) -> {
      Object v = f.get(o);
      if (v == null) {
        return "";
      }
      Enum<?> e = (Enum<?>) v;
      return e.name();
    });
  }

  public static String getString(Object obj, Field field) {
    return EntityFields.<String>getValue(obj, field);
  }

  public static DateTime getDateTime(Object obj, Field field) {
    return EntityFields.<DateTime>getValue(obj, field);
  }

  public static <T> void setField(T entity, Field field, Object value) {
    boolean unset = makeAccessibleIfNeeded(field);
    try {
      field.set(entity, value);
    } catch (IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
  }

  private static boolean makeAccessibleIfNeeded(Field field) {
    if (field.isAccessible()) {
      return false;
    }
    field.setAccessible(true);
    return true;
  }


  private EntityFields() {}
}
