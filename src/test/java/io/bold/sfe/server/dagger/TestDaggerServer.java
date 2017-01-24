package io.bold.sfe.server.dagger;

import dagger.Component;
import io.bold.sfe.server.TestServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

public class TestDaggerServer {
  private static final Logger log = LoggerFactory.getLogger(TestDaggerServer.class);

  @Singleton
  @Component(modules = ApplicationModule.class)
  public interface Server {
    TestServer server();
  }

  public static void main(String[] args) throws Exception {
    DaggerTestDaggerServer_Server.builder().build();
  }
}
