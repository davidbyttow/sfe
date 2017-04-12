package com.simplethingsllc.store.client.cache;

import com.google.inject.Inject;
import com.simplethingsllc.store.client.EntityStoreAsync;

import java.time.Duration;

public class CachingStoreFactory {

  private EntityStoreAsync entityStore;

  @Inject public CachingStoreFactory(EntityStoreAsync entityStore) {
    this.entityStore = entityStore;
  }

  public static class Builder<T> {
    private final CachingStore<T> store;

    Builder(CachingStore<T> store) {
      this.store = store;
    }

    public Builder<T> addFieldKey(FieldKey<T> fieldKey) {
      store.withFieldKey(fieldKey);
      return this;
    }

    public CachingStore<T> build() {
      return store;
    }
  }

  public <T> Builder<T> newBuilder(Class<T> type, Duration cachingDuration) {
    return new Builder<>(new CachingStore<>(type, cachingDuration, entityStore));
  }

  public EntityStoreAsync getEntityStore() {
    return entityStore;
  }
}
