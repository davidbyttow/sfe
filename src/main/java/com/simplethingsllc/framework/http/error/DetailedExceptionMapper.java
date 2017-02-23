package com.simplethingsllc.framework.http.error;

import com.google.common.base.Strings;
import io.bold.sfe.service.Env;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Priority(0)
public class DetailedExceptionMapper implements ExceptionMapper<Exception> {

  private static final Logger log = LoggerFactory.getLogger(DetailedExceptionMapper.class);

  private final Env env;
  private final ErrorMediaConverter errorMediaConverter;

  @Inject public DetailedExceptionMapper(Env env,
                                         ErrorMediaConverter errorMediaConverter) {
    this.env = env;
    this.errorMediaConverter = errorMediaConverter;
  }

  @Override public Response toResponse(Exception exception) {
    ErrorDetails details = new ErrorDetails();
    details.detailMessage = getStackTrace(exception);

    if (exception instanceof WebApplicationException) {
      WebApplicationException webException = (WebApplicationException) exception;
      int status = webException.getResponse().getStatus();
      if (status >= 300 && status < 400) {
        // If we didn't build a response, then let's check again.
        return webException.getResponse();
      }
      details = handleWebException(webException);
    }

    if (!Strings.isNullOrEmpty(details.detailMessage)) {
      log.error(ExceptionUtils.getStackTrace(exception));
    }

    return errorMediaConverter.convert(details);
  }

  private ErrorDetails handleWebException(WebApplicationException exception) {
    int status = exception.getResponse().getStatus();

    ErrorDetails details = new ErrorDetails();
    details.status = status;
    details.detailMessage = getStackTrace(exception);

    if (exception instanceof ClientException) {
      ClientException clientException = (ClientException) exception;
      details.type = clientException.getErrorType();
      if (clientException.getUserMessage() != null) {
        details.message = clientException.getUserMessage();
      }
    } else if (status == 401) {
      details.type = ErrorDetails.ERROR_UNAUTHORIZED;
    } else if (status < 500) {
      details.type = ErrorDetails.ERROR_BAD_REQUEST;
      details.detailMessage = "";
    } else {
      details.type = ErrorDetails.ERROR_INTERNAL;
      details.detailMessage = getStackTrace(exception);
    }
    return details;
  }

  private String getStackTrace(Throwable exception) {
    if (env.isProduction()) {
      return "";
    }
    return ExceptionUtils.getStackTrace(exception);
  }

  static class Data {
    String errorMessage;
  }
}
