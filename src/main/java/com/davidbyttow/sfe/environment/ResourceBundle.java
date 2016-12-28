package com.davidbyttow.sfe.environment;

import com.google.inject.Injector;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;

public class ResourceBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new ResourceModule());
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {

  }
}
