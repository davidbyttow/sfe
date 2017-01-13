package io.bold.sfe.server;

import com.google.inject.Injector;
import io.bold.sfe.inject.GuiceApplication;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.service.BasicServiceBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer extends GuiceApplication<TestConfig> {
  private static final Logger log = LoggerFactory.getLogger(TestServer.class);

  public static void main(String[] args) throws Exception {
    new TestServer().run(args);
  }

  @Override public String getName() {
    return "test";
  }

  @Override public void initialize(GuiceBootstrap<TestConfig> bootstrap) {
    bootstrap.addBundle(new BasicServiceBundle<>());
    bootstrap.addModule(new TestModule());
  }

  @Override public void run(Injector ij, TestConfig config, Environment environment) {
    JerseyEnvironment jersey = environment.jersey();
    jersey.register(GuiceTestResource.class);
  }
}
