package com.davidbyttow.sfe.inject;

import com.google.inject.Injector;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

/** Dropwizard {@link io.dropwizard.ConfiguredBundle} equivalent which also has access to the {@link Injector} */
public interface ConfiguredGuiceBundle<T extends Configuration> {
  /**
   * Initializes the application bootstrap.
   *
   * @param bootstrap the application bootstrap
   */
  void initialize(GuiceBootstrap<?> bootstrap);

  /**
   * Initializes the environment.
   *
   * @param injector The Injector
   * @param configuration    the configuration object
   * @param environment      the application's {@link io.dropwizard.setup.Environment}
   * @throws Exception if something goes wrong
   */
  void run(Injector injector, T configuration, Environment environment) throws Exception;
}
