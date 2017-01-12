package com.simplethingsllc.store.common;

public interface JsonSerializer {
  <T> T deserialize(byte[] bytes, Class<T> type);
}
