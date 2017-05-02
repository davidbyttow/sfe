package com.simplethingsllc.store.client.cache;

import io.bold.sfe.cache.CachePolicy;

public interface EntityCache<T> {
  T get(String id, CachePolicy policy);

  void put(T entity);

  void remove(String id);
}
