package com.simplethingsllc.store.client;

import com.simplethingsllc.store.server.EntityStoreImpl;

public class EntityStoreClient {

  private final EntityStore store;
  private final EntityStoreAsync asyncStore;

  EntityStoreClient(EntityStoreImpl store) {
    this.store = store;
    this.asyncStore = store;
  }

  public EntityStore getStore() {
    return store;
  }

  public EntityStoreAsync getAsyncStore() {
    return asyncStore;
  }
}
