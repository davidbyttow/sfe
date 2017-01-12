package com.simplethingsllc.store.client;

import java.util.List;

public interface EntityStore {
  void put(Object entity);

  void putMany(List<?> entities);

  <T> T get(String id, Class<T> type);

  <T> List<T> getMany(List<String> ids, Class<T> type);

  void delete(Object entity);

  void deleteMany(List<String> ids, Class<?> type);

  void delete(String id, Class<?> type);

  int count(Query<?> query);

  <T> List<T> fetch(Query<T> query);

  <T> QueryResults<T> fetchPage(Query<T> query);

  <T> QueryResults<T> fetchNext(QueryResults<T> page);
}
