package io.bold.sfe.config;

import io.bold.sfe.environment.aws.AwsCredentialsConfig;
import io.bold.sfe.integrations.IntegrationsConfig;
import io.bold.sfe.integrations.google.GoogleAuthConfig;
import io.bold.sfe.service.Env;
import io.bold.sfe.storage.StorageConfig;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Common configuration for all services.
 *
 * @author d
 */
public class BasicServiceConfig extends Configuration {

  @Valid @NotNull public Env env;
  @Valid public StorageConfig storage;
  @Valid public IntegrationsConfig integrations = new IntegrationsConfig();
  @Valid public AwsCredentialsConfig aws;
  @Valid public GoogleAuthConfig googleAuth;
  public Map<String, String> admins = new HashMap<>();
  public String assetBase = "";
}

