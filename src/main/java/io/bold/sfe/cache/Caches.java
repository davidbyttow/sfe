package io.bold.sfe.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public final class Caches {
  public static <K, V> Cache<K, V> newExpiringCache(long duration, TimeUnit unit) {
    return CacheBuilder.newBuilder()
      .expireAfterWrite(duration, unit)
      .build();
  }

  private Caches() {}
}
