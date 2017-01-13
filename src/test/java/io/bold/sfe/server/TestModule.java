package io.bold.sfe.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;

public class TestModule extends AbstractModule {

  @Override protected void configure() {
  }

  @Provides
  @TestObject.Instance
  TestObject instance() {
    System.out.println("instance");
    return new TestObject();
  }

  @Provides
  @RequestScoped
  @TestObject.RequestScoped
  TestObject requestScoped() {
    System.out.println("requestScoped");
    return new TestObject();
  }

  @Provides
  @Singleton
  @TestObject.Singleton
  TestObject singleton() {
    System.out.println("singleton");
    return new TestObject();
  }
}
