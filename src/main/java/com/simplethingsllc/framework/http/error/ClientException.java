package com.simplethingsllc.framework.http.error;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ClientException extends WebApplicationException {

  private final String errorType;
  private final String userMessage;

  public ClientException(String errorType) {
    super(Response.Status.BAD_REQUEST);
    this.errorType = errorType;
    this.userMessage = null;
  }

  public ClientException(String errorType, String userMessage) {
    super(Response.Status.BAD_REQUEST);
    this.errorType = errorType;
    this.userMessage = userMessage;
  }

  public String getErrorType() {
    return errorType;
  }

  @Nullable public String getUserMessage() {
    return userMessage;
  }
}
