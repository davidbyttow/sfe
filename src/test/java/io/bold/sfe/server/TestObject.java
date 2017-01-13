package io.bold.sfe.server;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class TestObject {

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
  public @interface Instance {}

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
  public @interface RequestScoped {}

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
  public @interface Singleton {}

  static class InstanceObject extends TestObject {}
  static class RequestScopedObject extends TestObject {}
  static class SingletonObject extends TestObject {}

  public int injectCount;
}
