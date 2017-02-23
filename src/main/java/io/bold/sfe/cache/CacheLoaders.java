package io.bold.sfe.cache;

import javax.annotation.Nullable;

public final class CacheLoaders {
  private CacheLoaders() {}

  @Nullable public static <K, V>  V load(K key, CachePolicy policy, CacheLoader<K, V> loader, LoadingCache<K, V> cache) {
    if (policy == CachePolicy.IN_MEMORY) {
      return cache.get(key, loader);
    }
    return loader.load(key);
  }

}
