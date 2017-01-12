package io.bold.sfe.storage;


import com.google.inject.Injector;
import com.simplethingsllc.store.client.EntityKind;
import com.simplethingsllc.store.client.EntityStoreClient;
import com.simplethingsllc.store.server.EntitiesStorage;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;

import java.util.HashSet;
import java.util.Set;

public final class StorageBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private final Set<String> packagePrefixes;

  public StorageBundle(Set<String> packagePrefixes) {
    this.packagePrefixes = packagePrefixes;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    Set<Class<?>> storageProviderClasses = new HashSet<>();
    Set<Class<?>> entityClasses = new HashSet<>();
    for (String packagePrefix : packagePrefixes) {
      storageProviderClasses.addAll(MoreReflections.getTypesAnnotatedWith(packagePrefix, StorageProvider.class));
      entityClasses.addAll(MoreReflections.getTypesAnnotatedWith(packagePrefix, EntityKind.class));
    }
    bootstrap.addModule(new EntityStoreModule(entityClasses));
    bootstrap.addModule(new StorageModule.DbModule<>(EntitiesStorage.class));
    bootstrap.addModule(new StorageModule(storageProviderClasses));
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    // Kickstart the process for entity store to be initialized.
    injector.getInstance(EntityStoreClient.class);
  }
}
