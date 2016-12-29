package io.bold.sfe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import io.bold.sfe.common.ProviderOnlyModule;
import io.bold.sfe.inject.LazySingleton;
import io.bold.sfe.service.AppName;
import io.bold.sfe.service.Env;
import io.dropwizard.configuration.ConfigurationSourceProvider;

import javax.validation.Validator;

/** Installs a {@link ComponentConfigResolver} that can be used to lookup component-specific configurations */
public final class ComponentConfigModule extends ProviderOnlyModule {
  @Provides @LazySingleton
  ComponentConfigResolver componentConfigResolver(Env env, @AppName String appName, Validator validator, ObjectMapper objectMapper) {
    ConfigurationSourceProvider sourceProvider = new EnvSpecificConfigurationSourceProvider(env, appName);
    return new ComponentConfigResolver(validator, objectMapper, sourceProvider);
  }
}
