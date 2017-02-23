package com.simplethingsllc.store.client.cache;

import com.simplethingsllc.store.client.EntityStoreAsync;
import com.simplethingsllc.store.common.Queries;
import io.bold.sfe.cache.CacheLoader;

import javax.annotation.Nullable;

public final class Loaders {

  static <T> CacheLoader<String, T> newIdLoader(EntityStoreAsync entityStore, Class<T> type) {
    return new CacheLoader<String, T>() {
      @Nullable @Override public T load(String key) {
        return entityStore.get(key, type);
      }
    };
  }

  static <T> CacheLoader<String, T> newSecondaryIdLoader(EntityStoreAsync entityStore, String fieldName, Class<T> type) {
    return new CacheLoader<String, T>() {
      @Nullable @Override public T load(String key) {
        return Queries.fetchBySecondaryId(entityStore, fieldName, key, type);
      }
    };
  }

  private Loaders() {}
}
