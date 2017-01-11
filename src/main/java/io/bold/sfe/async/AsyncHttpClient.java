package io.bold.sfe.async;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import io.bold.sfe.concurrent.BackgroundThreadPool;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class AsyncHttpClient {

  private final HttpClient httpClient;
  private final ListeningExecutorService executor;

  @Inject AsyncHttpClient(HttpClient httpClient, @BackgroundThreadPool ListeningExecutorService executor) {
    this.httpClient = httpClient;
    this.executor = executor;
  }

  public ListenableFuture<HttpResponse> executeAsync(HttpUriRequest request) {
    return executor.submit(() -> {
      HttpResponse resp = null;
      try {
        resp = httpClient.execute(request);
        return resp;
      } catch (IOException e) {
        throw Throwables.propagate(e);
      }
    });
  }
}
