package com.simplethingsllc.store.server;

import com.google.common.base.Strings;
import com.simplethingsllc.store.client.Query;

import java.util.ArrayList;
import java.util.List;

public class PageResults<T> {

  final Query<T> query;
  final List<T> entities = new ArrayList<>();
  String endCursor;

  PageResults(Query<T> query) {
    this.query = query;
  }

  public List<T> getEntities() {
    return entities;
  }

  public boolean hasMore() {
    return hasCursor();
  }

  public boolean hasCursor() {
    return !Strings.isNullOrEmpty(endCursor);
  }
}
