package com.simplethingsllc.framework.http.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class RedirectException extends WebApplicationException {
  public RedirectException(String path) {
    super(Response.status(Response.Status.TEMPORARY_REDIRECT)
      .header("Location", path)
      .build());
  }
}
