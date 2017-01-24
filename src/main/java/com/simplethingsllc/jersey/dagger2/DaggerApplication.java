package com.simplethingsllc.jersey.dagger2;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class DaggerApplication<T extends Configuration> extends Application<T> {

  @Override public final void initialize(Bootstrap<T> bootstrap) {
  }

  @Override public final void run(T configuration, Environment environment) throws Exception {
  }
}
