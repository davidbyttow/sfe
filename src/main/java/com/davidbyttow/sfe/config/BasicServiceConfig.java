package com.davidbyttow.sfe.config;

import com.davidbyttow.sfe.environment.aws.AwsCredentialsConfig;
import com.davidbyttow.sfe.integrations.IntegrationsConfig;
import com.davidbyttow.sfe.integrations.google.GoogleAuthConfig;
import com.davidbyttow.sfe.service.Env;
import com.davidbyttow.sfe.storage.ReplicatedDataSourceFactory;
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
  @NotNull public ReplicatedDataSourceFactory database;
  public Map<String, String> admins = new HashMap<>();
  public IntegrationsConfig integrations = new IntegrationsConfig();
  public AwsCredentialsConfig aws;
  public GoogleAuthConfig googleAuth;
  public String assetBase = "";
}

