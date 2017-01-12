package com.simplethingsllc.store.client;

import java.util.List;

public interface QueryResults<T> {
  List<T> getEntities();
  boolean hasMore();
}
