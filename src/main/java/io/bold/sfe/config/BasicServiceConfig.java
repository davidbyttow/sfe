package io.bold.sfe.config;

import io.bold.sfe.environment.aws.AwsCredentialsConfig;
import io.bold.sfe.integrations.IntegrationsConfig;
import io.bold.sfe.integrations.google.GoogleAuthConfig;
import io.bold.sfe.service.Env;
import io.bold.sfe.storage.ReplicatedDataSourceFactory;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Common configuration for all services.
 *
 * @author d
 */
public class BasicServiceConfig extends Configuration {

  @NotNull public Env env;
  public ReplicatedDataSourceFactory database;
  public Map<String, String> admins = new HashMap<>();
  public IntegrationsConfig integrations = new IntegrationsConfig();
  public AwsCredentialsConfig aws;
  public GoogleAuthConfig googleAuth;
  public String assetBase = "";
}

