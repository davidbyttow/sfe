package io.bold.sfe.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;

public final class JsonBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    Json.configureMapper(bootstrap.dropwizard().getObjectMapper());
    bootstrap.addModule(new AbstractModule() {
      @Override protected void configure() {
        bind(ObjectMapper.class).toInstance(Json.getObjectMapper());
      }
    });
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
  }
}
