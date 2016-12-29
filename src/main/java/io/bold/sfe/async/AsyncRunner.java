package io.bold.sfe.async;

import io.bold.sfe.concurrent.BackgroundThreadPool;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

import java.util.concurrent.Callable;

// TODO(d): This class is either a placeholder or will be replaced entirely with something that actually uses a
// async task processor.
public class AsyncRunner {

  private final ListeningExecutorService executor;

  @Inject AsyncRunner(@BackgroundThreadPool ListeningExecutorService executor) {
    this.executor = executor;
  }

  public ListenableFuture<?> runAsync(Runnable runnable) {
    return executor.submit(runnable);
  }

  public <T> ListenableFuture<T> runAsync(final Callable<T> callable) {
    return executor.submit(callable);
  }

  public <T> void addListener(ListenableFuture<T> future, Runnable listener) {
    future.addListener(listener, executor);
  }
}
