package io.bold.sfe.storage;

import com.google.inject.Injector;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.storage.entity.EntityMetadataService;
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
