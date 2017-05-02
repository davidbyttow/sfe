package com.simplethingsllc.store.client.cache;

import com.simplethingsllc.store.client.EntityStoreAsync;
import io.bold.sfe.cache.CacheLoader;
import io.bold.sfe.cache.CachePolicy;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CachingStore<T> extends AbstractEntityCache<T> {

  private final Class<T> type;
  private final Map<String, CacheLoader<String, T>> secondaryLoaders = new HashMap<>();
  private final Map<String, Function<T, String>> fieldExtractors = new HashMap<>();

  public CachingStore(Class<T> type, Duration cachingDuration, EntityStoreAsync entityStore) {
    super(type, cachingDuration, entityStore);
    this.type = type;
  }

  @Override public void put(T entity) {
    entityStore.put(entity);
    for (String key : secondaryLoaders.keySet()) {
      String keyValue = fieldExtractors.get(key).apply(entity);
      if (keyValue != null) {
        this.save(keyValue, entity);
      }
    }
  }

  @Override public void remove(String id) {
    entityStore.delete(id, type);
    this.delete(id);
  }

  CachingStore<T> withFieldKey(FieldKey<T> fieldKey) {
    String name = fieldKey.getName();
    secondaryLoaders.put(name, Loaders.newSecondaryIdLoader(entityStore, name, type));
    fieldExtractors.put(name, fieldKey.getExtractor());
    return this;
  }

  @Nullable public T getByField(FieldKey<T> fieldKey, String fieldValue, CachePolicy policy) {
    return load(fieldValue, policy, secondaryLoaders.get(fieldKey.getName()));
  }
}
