package io.bold.sfe.server.dagger;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public interface DaggerBundle<T extends Configuration> {
  void initialize(DaggerBundle<T> bootstrap);

  void run(T configuration, Environment environment) throws Exception;
}
