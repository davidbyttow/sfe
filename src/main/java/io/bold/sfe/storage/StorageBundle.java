package io.bold.sfe.storage;


import com.google.inject.Injector;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;

import java.util.HashSet;
import java.util.Set;

public final class StorageBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private final String databaseName;
  private final Set<String> packagePrefixes;

  public StorageBundle(String databaseName, Set<String> packagePrefixes) {
    this.databaseName = databaseName;
    this.packagePrefixes = packagePrefixes;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    Set<Class<?>> storageProviderClasses = new HashSet<>();
    for (String packagePrefix : packagePrefixes) {
      storageProviderClasses.addAll(MoreReflections.getTypesAnnotatedWith(packagePrefix, StorageProvider.class));
    }
    bootstrap.addModule(new StorageModule(databaseName, storageProviderClasses));
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
  }
}
