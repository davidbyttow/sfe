package io.bold.sfe.integrations.slack;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import io.bold.sfe.async.AsyncHttpClient;
import io.bold.sfe.common.QueryParams;
import io.bold.sfe.json.Json;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SlackApiClient {

  private static final Logger log = LoggerFactory.getLogger(SlackApiClient.class);

  private final AsyncHttpClient httpClient;

  @Inject public SlackApiClient(AsyncHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public <T> ListenableFuture<SlackApiResponse<T>> callMethodAsync(String method, @Nullable Map<String, String> data, Class<T> responseType) {
    return callAsync("https://slack.com/api/" + method, data, responseType);
  }

  public void callAsync(String url, Object data) {
    callAsync(url, data, null);
  }

  public <T> ListenableFuture<SlackApiResponse<T>> callAsync(String url, @Nullable Map<String, String> data, @Nullable Class<T> responseType) {
    HttpPost post = new HttpPost(url);
    try {
      if (data != null) {
        List<NameValuePair> params = QueryParams.createList(data);
        post.setEntity(new UrlEncodedFormEntity(params, Charsets.UTF_8));
      }
      return doCallAsync(post, responseType);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public <T> ListenableFuture<SlackApiResponse<T>> callAsync(String url, Object data, @Nullable Class<T> responseType) {
    HttpPost post = new HttpPost(url);
    try {
      post.setHeader("Content-Type", "application/json");
      if (data != null) {
        post.setEntity(new StringEntity(Json.writeValueAsString(data)));
      }
      return doCallAsync(post, responseType);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private <T> ListenableFuture<SlackApiResponse<T>> doCallAsync(HttpPost post, @Nullable Class<T> responseType) throws IOException {
    post.setHeader("Accept", "application/json");
    ListenableFuture<HttpResponse> future = httpClient.executeAsync(post);
    return Futures.transform(future, (Function<HttpResponse, SlackApiResponse<T>>) response -> {
      if (response.getStatusLine().getStatusCode() != 200) {
        log.error("Couldn't post to Slack, got error code: {}", response.getStatusLine().getStatusCode());
        return null;
      }
      String entityString = null;
      try {
        entityString = EntityUtils.toString(response.getEntity());
      } catch (IOException e) {
        throw Throwables.propagate(e);
      }

      SlackApiResponse<T> resp = new SlackApiResponse<>();

      resp.header = Json.readValue(entityString, SlackApiResponseHeader.class);
      if (!resp.header.ok) {
        log.error("Slack API uri={} error={} warning={}", post.getURI(), resp.header.error, resp.header.warning);
      } else {
        if (!Strings.isNullOrEmpty(resp.header.warning)) {
          log.warn("Slack API uri={} warning={}", post.getURI(), resp.header.warning);
        }
        if (responseType != null) {
          resp.data = Json.readValue(entityString, responseType);
        }
      }
      return resp;
    });
  }
}
