package com.davidbyttow.sfe.common;


import com.google.common.primitives.Primitives;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/** Helpers for dealing with types */
public final class Types {
  private static final Package functionalPackage = Package.getPackage("java.util.function");

  /** @return true if this is a bean type.  Not exhaustive, but should be good enough */
  public static boolean isBean(Class type) {
    return !isCollection(type)
        && !isFunctional(type)
        && !isPrimitiveWrapper(type)
        && !isPrimitive(type)
        && !Iterator.class.isAssignableFrom(type)
        && !String.class.isAssignableFrom(type)
        && !Object.class.equals(type)
        && !type.isArray()
        && !type.isEnum()
        && !Void.class.isAssignableFrom(type)
        && !ReadableInstant.class.isAssignableFrom(type)
        && !ReadableDuration.class.isAssignableFrom(type);
  }

  /** @return true if this is a functional type */
  public static boolean isFunctional(Class<?> type) {
    return type.getPackage() != null && type.getPackage().equals(functionalPackage);
  }

  /** @return true if this is a primitive type */
  public static boolean isPrimitive(Class type) {
    return int.class.isAssignableFrom(type)
        || short.class.isAssignableFrom(type)
        || long.class.isAssignableFrom(type)
        || float.class.isAssignableFrom(type)
        || double.class.isAssignableFrom(type)
        || byte.class.isAssignableFrom(type)
        || char.class.isAssignableFrom(type);
  }

  /** @return true if this is a collection type */
  public static boolean isCollection(Class type) {
    return Collection.class.isAssignableFrom(type)
        || Map.class.isAssignableFrom(type)
        || Stream.class.isAssignableFrom(type);
  }

  /** @return true if this is a primitive wrapper */
  public static boolean isPrimitiveWrapper(Class type) {
    return Primitives.isWrapperType(type);
  }


  private Types() {}
}
