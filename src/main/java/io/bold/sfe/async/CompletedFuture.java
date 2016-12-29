package io.bold.sfe.async;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class CompletedFuture<T> implements ListenableFuture<T> {
  private final T v;
  private final Throwable re;

  public CompletedFuture(T v, Throwable re) {
    this.v = v;
    this.re = re;
  }

  public static <T> CompletedFuture<T> create() {
    return new CompletedFuture<>(null, null);
  }

  public static <T> CompletedFuture<T> create(T v) {
    return new CompletedFuture<>(v, null);
  }

  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  public boolean isCancelled() {
    return false;
  }

  public boolean isDone() {
    return true;
  }

  public T get() throws ExecutionException {
    if(this.re != null) {
      throw new ExecutionException(this.re);
    } else {
      return this.v;
    }
  }

  public T get(long timeout, TimeUnit unit) throws ExecutionException {
    return this.get();
  }

  @Override public void addListener(Runnable listener, Executor executor) {
    executor.execute(listener);
  }
}
