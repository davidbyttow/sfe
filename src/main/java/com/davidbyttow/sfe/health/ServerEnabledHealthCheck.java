package com.davidbyttow.sfe.health;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/** Health check for manually controlling whether a service is enabled */
public class ServerEnabledHealthCheck extends HealthCheck {
  private static final Logger logger = LoggerFactory.getLogger(ServerEnabledHealthCheck.class);
  private final AtomicBoolean enabled = new AtomicBoolean(false);

  @Override protected Result check() throws Exception {
    return enabled.get() ? Result.healthy() : Result.unhealthy("Server is disabled");
  }

  public void enable() {
    logger.info("Enabling server");
    enabled.set(true);
  }

  public void disable() {
    logger.info("Disabling server");
    enabled.set(false);
  }
}
