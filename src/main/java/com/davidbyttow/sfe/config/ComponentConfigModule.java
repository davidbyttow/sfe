package com.davidbyttow.sfe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import com.davidbyttow.sfe.common.ProviderOnlyModule;
import com.davidbyttow.sfe.inject.LazySingleton;
import com.davidbyttow.sfe.service.AppName;
import com.davidbyttow.sfe.service.Env;
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
