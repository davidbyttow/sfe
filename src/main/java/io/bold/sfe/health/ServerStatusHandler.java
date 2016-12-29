package io.bold.sfe.health;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Reports the current status of the service, in a form suitable for registering with a load balancer.  Returns the
 * overall status of the service, along with any failing healthchecks and the current build version.  Response OK (200)
 * if the service is health, or 500 (INTERNAL_SERVER_ERROR) if the service is unhealthy.
 */
@Path("_status")
@Produces(MediaType.APPLICATION_JSON)
public class ServerStatusHandler {
  private final HealthCheckRegistry healthChecks;

  @Inject ServerStatusHandler(HealthCheckRegistry healthChecks) {
    this.healthChecks = Preconditions.checkNotNull(healthChecks);
  }

  @GET public Response checkStatus() {
    List<FailedHealthCheck> failedHealthChecks = checkServerHealth();
    ServerStatus status = new ServerStatus(failedHealthChecks.isEmpty(), failedHealthChecks);
    if (status.isOk()) {
      return Response.ok(status).build();
    } else {
      return Response.serverError().entity(status).build();
    }
  }

  private List<FailedHealthCheck> checkServerHealth() {
    Map<String, HealthCheck.Result> healthCheckResults = healthChecks.runHealthChecks();
    ImmutableList.Builder<FailedHealthCheck> failedHealthChecks = ImmutableList.builder();
    for (Map.Entry<String, HealthCheck.Result> healthCheckResult : healthCheckResults.entrySet()) {
      if (!healthCheckResult.getValue().isHealthy()) {
        failedHealthChecks.add(new FailedHealthCheck(healthCheckResult.getKey(),
            healthCheckResult.getValue().getMessage()));
      }
    }

    return failedHealthChecks.build();
  }
}
