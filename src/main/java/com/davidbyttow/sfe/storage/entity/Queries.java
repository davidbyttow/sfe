package com.davidbyttow.sfe.storage.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class Queries {
  private static final Logger log = LoggerFactory.getLogger(Queries.class);

  public static <T> Query<T> kind(Class<T> type, int limit) {
    return Query.newBuilder(type).limit(limit).build();
  }

  public static <T> T fetchOnly(EntityStore entityStore, Query<T> query) {
    List<T> found = entityStore.fetch(query);
    if (found.isEmpty()) {
      return null;
    }

    if (found.size() > 1) {
      log.error("Entity kind={} query has {} results, only one expected", query.getType().getName(), found.size());
      return null;
    }

    return found.get(0);
  }

  // TODO(d): Reconcile this with fetchOnly
  public static <T> T fetchBySecondaryId(EntityStore entityStore, String name, String value, Class<T> type) {
    List<T> found = fetchAllBySecondaryId(entityStore, name, value, type);
    if (found.isEmpty()) {
      return null;
    }

    if (found.size() > 1) {
      log.error("Entity kind={} with secondary id={} has {} results", type.getName(), name, found.size());
      return null;
    }

    return found.get(0);
  }

  public static <T> List<T> fetchAllBySecondaryId(EntityStore entityStore, String name, String value, Class<T> type) {
    Query<T> query = Query.newBuilder(type)
      .addFilter(name, Query.Equality.Equals, value)
      .build();

    return entityStore.fetch(query);
  }

  public static <T> Query<T> secondary(String fieldName, String fieldValue, Class<T> type) {
    return Query.newBuilder(type)
      .addFilter(fieldName, Query.Equality.Equals, fieldValue)
      .build();
  }

  public static <T> int deleteAll(EntityStore entityStore, Query<T> query) {
    AtomicInteger count = new AtomicInteger(0);
    entityStore.fetch(query).parallelStream().forEach((e) -> {
      entityStore.delete(e);
      count.incrementAndGet();
    });
    return count.get();
  }

  private Queries() {}
}
