package io.bold.sfe.async;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import io.bold.sfe.concurrent.BackgroundThreadPool;

import java.util.ArrayList;
import java.util.List;

public class Barrier {

  private final ListeningExecutorService executor;
  private final List<ListenableFuture<?>> futures = new ArrayList<>();

  @Inject public Barrier(@BackgroundThreadPool ListeningExecutorService executor) {
    this.executor = executor;
  }

  public ListenableFuture<?> add(Runnable call) {
    ListenableFuture<?> future = executor.submit(call);
    futures.add(future);
    return future;
  }

  public void waitForAll() {
    ListenableFuture<List<Object>> future = Futures.allAsList(futures);
    Futures.getUnchecked(future);
  }
}
