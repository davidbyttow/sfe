package io.bold.sfe.storage.entity;

public interface EntityHandler<T> {
  T onBeforeSave(T entity);
}
