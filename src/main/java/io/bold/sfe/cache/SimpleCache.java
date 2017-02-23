package io.bold.sfe.cache;

public interface SimpleCache<K, V> {
  V get(K key);

  void put(K key, V object);

  void remove(K key);
}
