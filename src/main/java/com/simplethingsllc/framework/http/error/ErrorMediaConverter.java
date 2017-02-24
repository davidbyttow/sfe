package com.simplethingsllc.framework.http.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

class ErrorMediaConverter {
  private static final Logger log = LoggerFactory.getLogger(ErrorMediaConverter.class);

  Response convert(ErrorDetails errorDetails) {
    return Response.status(errorDetails.status)
      .entity(errorDetails)
      .build();
  }
}
