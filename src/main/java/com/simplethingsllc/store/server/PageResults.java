package com.simplethingsllc.store.server;

import com.google.common.base.Strings;
import com.simplethingsllc.store.client.Query;
import com.simplethingsllc.store.client.QueryResults;

import java.util.ArrayList;
import java.util.List;

class PageResults<T> implements QueryResults<T> {

  final Query<T> query;
  final List<T> entities = new ArrayList<>();
  String endCursor;

  PageResults(Query<T> query) {
    this.query = query;
  }

  @Override public List<T> getEntities() {
    return entities;
  }

  @Override public boolean hasMore() {
    return hasCursor();
  }

  boolean hasCursor() {
    return !Strings.isNullOrEmpty(endCursor);
  }
}
