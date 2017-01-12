package io.bold.sfe.storage;


import com.google.inject.Injector;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import com.simplethingsllc.store.client.EntityKind;
import com.simplethingsllc.store.server.EntityMetadataService;
import com.simplethingsllc.store.client.EntityStoreModule;
import io.dropwizard.setup.Environment;

import java.util.HashSet;
import java.util.Set;

public final class StorageBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private final String databaseName;
  private final String entityIndexFile;
  private final Set<String> packagePrefixes;

  public StorageBundle(String databaseName, String entityIndexFile, Set<String> packagePrefixes) {
    this.databaseName = databaseName;
    this.entityIndexFile = entityIndexFile;
    this.packagePrefixes = packagePrefixes;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    Set<Class<?>> storageProviderClasses = new HashSet<>();
    Set<Class<?>> entityClasses = new HashSet<>();
    for (String packagePrefix : packagePrefixes) {
      storageProviderClasses.addAll(MoreReflections.getTypesAnnotatedWith(packagePrefix, StorageProvider.class));
      entityClasses.addAll(MoreReflections.getTypesAnnotatedWith(packagePrefix, EntityKind.class));
    }
    bootstrap.addModule(new StorageModule(databaseName, storageProviderClasses));
    bootstrap.addModule(new EntityStoreModule(entityIndexFile, entityClasses));
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    EntityMetadataService metadataService = injector.getInstance(EntityMetadataService.class);
    metadataService.updateAll();
  }
}
