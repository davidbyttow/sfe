package io.bold.sfe.inject;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import io.bold.sfe.service.AppName;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

import javax.validation.Validator;

/** Exposes Dropwizard environment resources used by the runtime that Hubspot doesn't */
final class DropwizardEnvironmentModule<T extends Configuration> extends AbstractModule {
  private final Class<T> configClass;
  private Environment environment;
  private Configuration config;

  DropwizardEnvironmentModule(Class<T> configClass) {
    this.configClass = configClass;
  }

  void setEnvironment(Environment environment) {
    this.environment = Preconditions.checkNotNull(environment);
  }

  void setConfiguration(Configuration config) {
    this.config = Preconditions.checkNotNull(config);
  }

  @SuppressWarnings("unchecked")
  @Override protected void configure() {
    // We skip the actual config class and stop at Configuration because they will be registered by dropwizard-guice.
    Class cc = configClass.getSuperclass();
    while (cc != Configuration.class && cc != Object.class) {
      bind(cc).toProvider(new Provider() {
        @Override public Object get() {
          return config;
        }
      });
      cc = cc.getSuperclass();
    }

    bind(Validator.class).toProvider(() -> {
      return environment.getValidator();
    });
    bind(MetricRegistry.class).toProvider(() -> {
      return environment.metrics();
    });
  }

  @Provides @AppName public String provideAppName() {
    return environment.getName();
  }
}
