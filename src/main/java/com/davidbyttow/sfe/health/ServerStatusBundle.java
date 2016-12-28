package com.davidbyttow.sfe.health;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/** Bundle registering server status support */
public final class ServerStatusBundle implements Bundle {
  @Override public void initialize(Bootstrap<?> bootstrap) {}

  @Override public void run(Environment environment) {
    ServerEnabledHealthCheck healthCheck = new ServerEnabledHealthCheck();
    environment.healthChecks().register("server-enabled", healthCheck);
    environment.admin().addTask(new EnableServerTask(healthCheck));
    environment.admin().addTask(new DisableServerTask(healthCheck));
    environment.jersey().register(ServerStatusHandler.class);
  }
}
