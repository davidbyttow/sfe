package io.bold.sfe.cache;

import com.google.common.cache.Cache;

import javax.annotation.Nullable;

public class InMemoryLoadingCache<K, V> implements LoadingCache<K, V> {

  public static <K, V> InMemoryLoadingCache<K, V> create(Cache<K, V> cache) {
    return new InMemoryLoadingCache<>(cache, null);
  }

  public static <K, V> InMemoryLoadingCache<K, V> create(Cache<K, V> cache, CacheLoader<K, V> loader) {
    return new InMemoryLoadingCache<>(cache, loader);
  }

  private final Cache<K, V> cache;
  private final CacheLoader<K, V> defaultLoader;

  InMemoryLoadingCache(Cache<K, V> cache, @Nullable CacheLoader<K, V> defaultLoader) {
    this.cache = cache;
    this.defaultLoader = defaultLoader;
  }

  @Override public V get(K key) {
    return getAndLoad(key, defaultLoader);
  }

  @Override public V get(K key, CacheLoader<K, V> loader) {
    return getAndLoad(key, loader);
  }

  @Override public void put(K key, V object) {
    cache.put(key, object);
  }

  @Override public void remove(K key) {
    cache.invalidate(key);
  }

  private V getAndLoad(K key, CacheLoader<K, V> loader) {
    V object = cache.getIfPresent(key);
    if (object == null) {
      object = loader.load(key);

      // Negative caching is not currently supported.
      if (object != null) {
        cache.put(key, object);
      }
    }
    return object;
  }
}
