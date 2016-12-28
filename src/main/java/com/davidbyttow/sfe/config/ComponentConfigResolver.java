package com.davidbyttow.sfe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;

import javax.validation.Validator;
import java.io.IOException;

/** Loads and validates configurations for internal components, allowing them to bundled inside of shared jars */
public class ComponentConfigResolver {
  private final Validator validator;
  private final ObjectMapper objectMapper;
  private final ConfigurationSourceProvider configurationSourceProvider;

  public ComponentConfigResolver(Validator validator, ObjectMapper objectMapper, ConfigurationSourceProvider configurationSourceProvider) {
    this.validator = Preconditions.checkNotNull(validator);
    this.objectMapper = Preconditions.checkNotNull(objectMapper);
    this.configurationSourceProvider = Preconditions.checkNotNull(configurationSourceProvider);
  }

  public <T> T resolve(String path, Class<T> type) {
    try {
      return new YamlConfigurationFactory<>(type, validator, objectMapper, "dw").build(configurationSourceProvider, path);
    } catch (IOException | ConfigurationException e) {
      throw Throwables.propagate(e);
    }
  }
}
