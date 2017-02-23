package com.simplethingsllc.framework.http.error;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class UnauthorizedException extends ClientErrorException {
  public UnauthorizedException() {
    super(Response.Status.UNAUTHORIZED);
  }
}
