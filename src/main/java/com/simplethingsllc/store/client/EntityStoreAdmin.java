package com.simplethingsllc.store.client;

import com.simplethingsllc.store.server.EntityMetadata;

import java.util.Map;

// TODO(d): Split this up appropriately.
public interface EntityStoreAdmin {
  String getEntityKind(Object entity);

  String getEntityId(Object entity);

  Class<?> getEntityType(Object entity);

  Class<?> getKindType(String kind);

  int backfillIndexForKind(String kind);

  Map<Class<?>, EntityMetadata> getEntityMetadataMap();
}
