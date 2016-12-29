package io.bold.sfe.environment;

import com.google.inject.Injector;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;

public class ResourceBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new ResourceModule());
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {

  }
}
