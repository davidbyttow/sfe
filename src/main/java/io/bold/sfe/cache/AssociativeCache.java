package io.bold.sfe.cache;

import javax.annotation.Nullable;

public interface AssociativeCache {

  boolean exists(String key);

  @Nullable <T> T get(String key, Class<T> type);

  <T> void set(String key, T object);

  void delete(String key);
}
