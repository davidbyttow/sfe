package io.bold.sfe.common;

import com.google.common.base.Throwables;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

public final class MoreReflections {

  public static Set<Class<?>> getTypesAnnotatedWith(String packagePrefix, Class<? extends Annotation> annotation) {
    Reflections reflections = new Reflections(packagePrefix);
    return reflections.getTypesAnnotatedWith(annotation);
  }

  public static Class<?> getFieldComponentType(Field field) {
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

  public static <T> T forceNewInstance(Class<T> type) {
    try {
      Constructor<T> c = type.getConstructor();
      c.setAccessible(true);
      return c.newInstance();
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private MoreReflections() {}
}
