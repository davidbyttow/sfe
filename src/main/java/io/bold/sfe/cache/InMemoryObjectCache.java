package io.bold.sfe.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

@Singleton
public class InMemoryObjectCache implements ObjectCache {

  private final Cache<String, Object> cache = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.DAYS)
      .expireAfterAccess(4, TimeUnit.HOURS)
      .maximumSize(100000)
      .softValues()
      .build();

  @Override public boolean exists(String key) {
    return cache.getIfPresent(key) != null;
  }

  @Nullable @Override public <T> T get(String key, Class<T> type) {
    Object object = cache.getIfPresent(key);
    return (object != null) ? type.cast(object) : null;
  }

  @Override public <T> void set(String key, T object) {
    cache.put(key, object);
  }

  @Override public void delete(String key) {
    cache.invalidate(key);
  }
}
