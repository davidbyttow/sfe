package io.bold.sfe.environment.aws;

import com.google.common.base.Preconditions;
import io.bold.sfe.common.MoreStrings;
import io.bold.sfe.service.InstanceHostProvider;
import io.bold.sfe.service.InstanceMetadata;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.net.URI;

/**
 * @author d
 */
public class AwsInstanceMetadata implements InstanceMetadata {
  private static final String BASE_URI = "http://instance-data/latest/meta-data/";

  private final HttpClient http;

  public AwsInstanceMetadata(HttpClient http) {
    this.http = http;
  }

  public AwsInstanceMetadata() {
    this(HttpClientBuilder.create().build());
  }

  @Override public String getRegion() {
    return getAttribute("placement/availability-zone");
  }

  @Override public String getInstanceName() {
    return getAttribute("hostname").replaceAll("([^\\.]+)\\..+", "$1");
  }

  @Override public InstanceHostProvider getPlatform() {
    return InstanceHostProvider.AWS;
  }

  @Override public boolean isLocal() {
    return false;
  }

  public static boolean isAvailable() {
    try {
      new AwsInstanceMetadata().getRegion();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private String getAttribute(String attributeName) {
    try {
      HttpGet get = new HttpGet(URI.create(BASE_URI + attributeName));
      HttpResponse response = http.execute(get);
      Preconditions.checkState(response.getStatusLine().getStatusCode() == 200, "Error retrieving metadata from %s: %s",
          attributeName, response.getStatusLine().getStatusCode());
      return MoreStrings.fromUtf8(response.getEntity().getContent());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
