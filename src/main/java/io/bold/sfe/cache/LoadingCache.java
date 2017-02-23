package io.bold.sfe.cache;

import com.google.common.util.concurrent.ListenableFuture;

public interface LoadingCache<K, V> extends SimpleCache<K, V> {
  V get(K key, CacheLoader<K, V> loader);

  ListenableFuture<V> getAsync(K key, AsyncCacheLoader<K, V> loader);
}
