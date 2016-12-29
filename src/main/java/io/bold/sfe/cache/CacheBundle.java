package io.bold.sfe.cache;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
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
