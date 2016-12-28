package com.davidbyttow.sfe.storage;

import com.google.inject.Injector;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import com.davidbyttow.sfe.storage.entity.EntityMetadataService;
import io.dropwizard.setup.Environment;

public final class StorageBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private final String databaseName;
  private final String packagePrefix;

  public StorageBundle(String databaseName, String packagePrefix) {
    this.databaseName = databaseName;
    this.packagePrefix = packagePrefix;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new StorageModule(databaseName, packagePrefix));
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    EntityMetadataService metadataService = injector.getInstance(EntityMetadataService.class);
    metadataService.updateAll();
  }
}
