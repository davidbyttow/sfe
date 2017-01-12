package com.simplethingsllc.store.client;

public interface EntityHandler<T> {
  T onBeforeSave(T entity);
}
