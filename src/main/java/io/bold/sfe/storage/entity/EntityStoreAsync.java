package io.bold.sfe.storage.entity;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public interface EntityStoreAsync {
  ListenableFuture<Void> putAsync(Object entity);

  ListenableFuture<Void> putManyAsync(List<?> entities);

  <T> ListenableFuture<T> getAsync(String id, Class<T> type);

  <T> ListenableFuture<List<T>> getManyAsync(List<String> ids, Class<T> type);

  ListenableFuture<Void> deleteAsync(Object entity);

  ListenableFuture<Void> deleteManyAsync(List<String> ids, Class<?> type);

  ListenableFuture<Void> deleteAsync(String id, Class<?> type);

  ListenableFuture<Integer> countAsync(Query<?> query);

  <T> ListenableFuture<List<T>> fetchAsync(Query<T> query);
}
