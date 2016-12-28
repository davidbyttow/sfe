package com.davidbyttow.sfe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.davidbyttow.sfe.service.Env;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;

import javax.validation.Validator;
import java.io.IOException;

/** WTF dropwizard.  So Spring-like... */
public class EnvSpecificConfigFactoryFactory<T extends BasicServiceConfig> implements ConfigurationFactoryFactory<T> {
  private final Env env;

  public EnvSpecificConfigFactoryFactory(Env env) {
    this.env = Preconditions.checkNotNull(env);
  }

  @Override
  public ConfigurationFactory<T> create(Class<T> klass, Validator validator, ObjectMapper objectMapper, String propertyPrefix) {
    return new YamlConfigurationFactory<T>(klass, validator, objectMapper, propertyPrefix) {
      @Override public T build(ConfigurationSourceProvider provider, String path)
          throws IOException, ConfigurationException {
        // Override / set env.  Sadly the only way to do this is through system properties
        try {
          System.setProperty(propertyPrefix + ".env", env.getId());
          return super.build(provider, path);
        } finally {
          System.clearProperty(propertyPrefix + ".env");
        }
      }
    };
  }
}
