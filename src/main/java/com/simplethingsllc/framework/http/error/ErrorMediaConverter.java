package com.simplethingsllc.framework.http.error;

import com.google.common.base.Strings;
import io.bold.sfe.web.PageViewRenderer;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

class ErrorMediaConverter {
  private static final Logger log = LoggerFactory.getLogger(ErrorMediaConverter.class);

  private final Provider<ContainerRequest> containerRequestProvider;
  private final PageViewRenderer pageViewRenderer;

  @Inject ErrorMediaConverter(Provider<ContainerRequest> containerRequestProvider,
                              PageViewRenderer pageViewRenderer) {
    this.containerRequestProvider = containerRequestProvider;
    this.pageViewRenderer = pageViewRenderer;
  }

  Response convert(ErrorDetails errorDetails) {
    ContainerRequest containerRequest = containerRequestProvider.get();
    String subType = "";
    if (containerRequest != null) {
      MediaType mediaType = containerRequest.getMediaType();
      if (mediaType != null) {
        subType = mediaType.getSubtype();
      }
      if (Strings.isNullOrEmpty(subType)) {
        if (containerRequest.getMethod().equals("GET")) {
          subType = "html";
        }
      }
    }

    switch (subType) {
      case "json":
        return Response.status(errorDetails.status)
          .entity(errorDetails)
          .build();
      case "html":
        // TODO(d): Handle this differently
        return Response.status(errorDetails.status)
          .entity(errorDetails)
          .build();
      default:
        return Response.status(errorDetails.status)
          .entity(errorDetails)
          .build();
    }
  }
}
