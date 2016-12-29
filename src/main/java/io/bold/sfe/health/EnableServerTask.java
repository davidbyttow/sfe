package io.bold.sfe.health;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;

import java.io.PrintWriter;

/** TaskHandler to disable a service */
public class EnableServerTask extends Task {
  private final ServerEnabledHealthCheck healthCheck;

  protected EnableServerTask(ServerEnabledHealthCheck healthCheck) {
    super("enable-server");
    this.healthCheck = Preconditions.checkNotNull(healthCheck);
  }

  @Override
  public void execute(ImmutableMultimap<String, String> params, PrintWriter pw)
      throws Exception {
    healthCheck.enable();
    pw.println("Server is enabled");
  }
}
