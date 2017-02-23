package io.bold.sfe.cache;

import com.google.common.util.concurrent.ListenableFuture;

public interface AsyncCache<K, V> {
  ListenableFuture<V> getAsync(K key);

  ListenableFuture<Void> putAsync(K key, V object);
}
