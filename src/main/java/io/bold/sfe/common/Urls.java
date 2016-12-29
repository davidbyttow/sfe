package io.bold.sfe.common;

import com.google.common.base.Preconditions;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public final class Urls {

  public static String appendParam(String url, String key, Object ...values) {
    Preconditions.checkArgument(values.length > 0);
    URI uri = URI.create(url);
    UriBuilder builder = UriBuilder.fromUri(uri);
    builder.queryParam(key, values);
    builder.replaceQueryParam(key, values);
    return builder.toString();
  }

  private Urls() {}
}
