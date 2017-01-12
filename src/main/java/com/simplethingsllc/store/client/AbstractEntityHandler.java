package com.simplethingsllc.store.client;

public abstract class AbstractEntityHandler<T> implements EntityHandler<T> {
  @Override public T onBeforeSave(T entity) {
    return entity;
  }
}
