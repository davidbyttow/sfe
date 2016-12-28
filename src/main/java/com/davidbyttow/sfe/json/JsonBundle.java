package com.davidbyttow.sfe.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
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
