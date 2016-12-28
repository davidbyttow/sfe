package com.davidbyttow.sfe.storage.entity;

import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface EntityIdRef {

  String getId(Object entity);

  void setId(Object entity, String id);

  class FieldEntityIdRef implements EntityIdRef {
    private final Field field;

    FieldEntityIdRef(Field field) {
      this.field = field;
    }

    @Override public String getId(Object entity) {
      return EntityFields.getString(entity, field);
    }

    @Override public void setId(Object entity, String id) {
      EntityFields.setField(entity, field, id);
    }
  }

  class MethodEntityIdRef implements EntityIdRef {
    private final Method method;

    MethodEntityIdRef(Method method) {
      this.method = method;
    }

    @Override public String getId(Object entity) {
      boolean unset = false;
      if (!method.isAccessible()) {
        unset = true;
        method.setAccessible(true);
      }
      try {
        return (String) method.invoke(entity);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw Throwables.propagate(e);
      } finally {
        if (unset) {
          method.setAccessible(false);
        }
      }
    }

    @Override public void setId(Object entity, String id) {
      // The id is synthesized, so just check that it matches.
      String synthesizedId = getId(entity);
      if (!id.equals(synthesizedId)) {
        throw new IllegalStateException(String.format("Synthesized id '%s' does not mwatch given id '%s'", synthesizedId, id));
      }
    }
  }
}
