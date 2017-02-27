package com.simplethingsllc.store.client.cache;

import com.simplethingsllc.store.client.EntityStoreAsync;
import io.bold.sfe.cache.CacheLoader;
import io.bold.sfe.cache.CacheLoaders;
import io.bold.sfe.cache.CachePolicy;
import io.bold.sfe.cache.Caches;
import io.bold.sfe.cache.InMemoryLoadingCache;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEntityCache<T> implements EntityCache<T> {

  protected final EntityStoreAsync entityStore;
  private final InMemoryLoadingCache<String, T> cache;
  private final CacheLoader<String, T> loadById;

  public AbstractEntityCache(Class<T> type, Duration cachingDuration, EntityStoreAsync entityStore) {
    this.entityStore = entityStore;
    this.loadById = Loaders.newIdLoader(entityStore, type);
    this.cache = InMemoryLoadingCache.create(
      Caches.newExpiringCache(cachingDuration.getSeconds(), TimeUnit.SECONDS));
  }

  @Override public T get(String id, CachePolicy policy) {
    return load(id, policy, loadById);
  }

  protected T load(String key, CachePolicy policy, CacheLoader<String, T> loader) {
    return CacheLoaders.load(key, policy, loader, cache);
  }

  protected void save(String key, T entity) {
    cache.put(key, entity);
  }
}
