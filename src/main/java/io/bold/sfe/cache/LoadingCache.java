package io.bold.sfe.cache;

public interface LoadingCache<K, V> extends SimpleCache<K, V> {
  V get(K key, CacheLoader<K, V> loader);
}
