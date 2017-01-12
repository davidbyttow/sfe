package com.simplethingsllc.store.common;

import com.google.common.base.Throwables;

import java.lang.reflect.Constructor;

public class MoreObjects {
  public static <T> T newInstance(Class<T> type) {
    try {
      Constructor<T> c = type.getConstructor();
      c.setAccessible(true);
      return c.newInstance();
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
