package io.bold.sfe.cache;

import com.google.common.util.concurrent.ListenableFuture;

public interface AsyncCacheLoader<T, U> extends CacheLoader<T,U> {
  ListenableFuture<U> loadAsync(T key);
}
