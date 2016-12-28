package com.davidbyttow.sfe.storage.entity;

public interface EntityHandler<T> {
  T onBeforeSave(T entity);
}
