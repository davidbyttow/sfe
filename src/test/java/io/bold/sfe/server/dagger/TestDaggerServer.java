package io.bold.sfe.server.dagger;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDaggerServer {
  private static final Logger log = LoggerFactory.getLogger(TestDaggerServer.class);

  @Module
  public static class AppModule {
    @Provides
    TestDaggerServer provideServer() {
      return new TestDaggerServer();
    }
  }

  @Component(modules = AppModule.class)
  public interface AppServer {
    TestDaggerServer server();
  }

  public static void main(String[] args) throws Exception {
    DaggerTestDaggerServer_AppServer.builder()
      .appModule(new AppModule())
      .build();
  }
}
