package io.bold.sfe.storage.entity;

public abstract class AbstractEntityHandler<T> implements EntityHandler<T> {
  @Override public T onBeforeSave(T entity) {
    return entity;
  }
}
