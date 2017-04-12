package com.simplethingsllc.store.client.cache;

import io.bold.sfe.cache.CachePolicy;

public class ForwardingCachingStore<T> implements EntityCache<T> {

  protected final CachingStore<T> store;

  public ForwardingCachingStore(CachingStore<T> store) {
    this.store = store;
  }

  public T get(String id) {
    return store.get(id, CachePolicy.IN_MEMORY);
  }

  @Override public T get(String id, CachePolicy cachePolicy) {
    return store.get(id, cachePolicy);
  }

  @Override public void put(T entity) {
    store.put(entity);
  }
}
