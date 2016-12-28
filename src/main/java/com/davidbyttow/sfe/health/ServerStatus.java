package com.davidbyttow.sfe.health;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class ServerStatus {
  @JsonProperty private final boolean ok;
  @JsonProperty private final List<FailedHealthCheck> failedHealthChecks;

  @JsonCreator
  public ServerStatus(@JsonProperty("ok") boolean ok,
                      @JsonProperty("failedHealthChecks") List<FailedHealthCheck> failedHealthChecks) {
    this.ok = ok;
    this.failedHealthChecks = ImmutableList.copyOf(failedHealthChecks);
  }

  public boolean isOk() {
    return ok;
  }

  public List<FailedHealthCheck> getFailedHealthChecks() {
    return failedHealthChecks;
  }
}
