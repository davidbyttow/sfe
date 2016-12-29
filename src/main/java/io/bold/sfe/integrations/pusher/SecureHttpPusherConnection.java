package io.bold.sfe.integrations.pusher;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import io.bold.sfe.common.Hashes;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecureHttpPusherConnection implements PusherConnection {

  private static final Logger log = LoggerFactory.getLogger(SecureHttpPusherConnection.class);

  private final String BASE_URL = "https://api.pusherapp.com";

  private final HttpClient httpClient;
  private final int appId;
  private final String appKey;
  private final String appSecret;
  private final String appUrl;

  public SecureHttpPusherConnection(HttpClient httpClient, int appId, String appKey, String appSecret) {
    this.httpClient = httpClient;
    this.appId = appId;
    this.appKey = appKey;
    this.appSecret = appSecret;
    this.appUrl = String.format("%s%s", BASE_URL, getAppPath(appId));
  }

  @Override public void send(String body) {
    String url = buildAuthUrl(getAppPath(appId), body);
    post(url, body);
  }

  private void post(String url, String payload) {
    HttpPost post = new HttpPost(url);
    StringEntity entity = new StringEntity(payload, Charsets.UTF_8);
    post.setEntity(entity);
    post.setHeader("Content-type", "application/json");
    try {
      HttpResponse response = httpClient.execute(post);
      if (response.getStatusLine().getStatusCode() != 200) {
        String entityString = "";
        try {
          entityString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
          // Ignored
        }
        log.error("Couldn't post to Pusher, got error code: {}: {}", response.getStatusLine().getStatusCode(), entityString);
      }
    } catch (IOException e) {
      log.error("Couldn't post to Pusher", e);
    }
  }

  private String buildAuthUrl(String path, String body) {
    Map<String, String> allParams = new HashMap<>();
    allParams.put("auth_key", appKey);
    allParams.put("auth_version", "1.0");
    allParams.put("auth_timestamp", Long.toString(System.currentTimeMillis() / 1000));
    if (!Strings.isNullOrEmpty(body)) {
      allParams.put("body_md5", Hashes.md5(body));
    }

    List<String> params = allParams.keySet()
      .stream()
      .sorted()
      .map((key) -> String.format("%s=%s", key, allParams.get(key)))
      .collect(Collectors.toList());

    String queryString = Joiner.on('&').join(params);
    String signatureString = String.format("POST\n%s\n%s", path, queryString);
    String signature = PusherSecretSigner.sign(signatureString, appSecret);

    allParams.put("auth_signature", signature);

    try {
      URIBuilder uri = new URIBuilder(appUrl);
      for (Map.Entry<String, String> entry : allParams.entrySet()) {
        uri.addParameter(entry.getKey(), entry.getValue());
      }
      return uri.build().toString();
    } catch (URISyntaxException e) {
      throw Throwables.propagate(e);
    }
  }

  private static String getAppPath(int appId) {
    return String.format("/apps/%d/events", appId);
  }
}
