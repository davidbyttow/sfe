package com.davidbyttow.sfe.storage.entity;

import com.google.common.base.Strings;

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

  boolean hasCursor() {
    return !Strings.isNullOrEmpty(endCursor);
  }
}
