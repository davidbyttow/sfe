package com.davidbyttow.sfe.config;

import com.google.common.collect.ImmutableMap;
import com.davidbyttow.sfe.service.Env;

import java.util.function.Function;

/**
 * {@link io.dropwizard.configuration.ConfigurationSourceProvider} that allows the config to be
 * parameterized by environment. Pulls the environment from a system property.
 */
public class EnvSpecificConfigurationSourceProvider extends HandlebarsConfigSourceProvider {
  public EnvSpecificConfigurationSourceProvider(Env env, String appName) {
    super(ImmutableMap.of("env", environment(env), "app", appName));
  }

  /** @return A {@link java.util.function.Function} for accessing properties of the environment */
  public static Function<String, Object> environment(Env env) {
    return (key) -> {
      if ("name".equals(key)) {
        return env.getId();
      }

      return env.getId().equalsIgnoreCase(key);
    };
  }
}
