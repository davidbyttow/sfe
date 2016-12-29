package io.bold.sfe.health;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class FailedHealthCheck {
  @JsonProperty private final String checkName;
  @JsonProperty private final String message;

  @JsonCreator
  public FailedHealthCheck(@JsonProperty("checkName") String checkName, @JsonProperty("message") String message) {
    this.checkName = Preconditions.checkNotNull(checkName);
    this.message = Preconditions.checkNotNull(message);
  }

  public String getCheckName() {
    return checkName;
  }

  public String getMessage() {
    return message;
  }
}
