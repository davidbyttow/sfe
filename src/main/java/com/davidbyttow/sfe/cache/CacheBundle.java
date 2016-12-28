package com.davidbyttow.sfe.cache;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;

public final class CacheBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new AbstractModule() {
      @Override protected void configure() {
        bind(ObjectCache.class).to(InMemoryObjectCache.class);
      }
    });
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
  }
}
