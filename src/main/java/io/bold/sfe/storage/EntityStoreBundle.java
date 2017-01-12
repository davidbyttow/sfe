package io.bold.sfe.storage;

import com.google.inject.Injector;
import com.simplethingsllc.store.client.EntityKind;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;

import java.util.HashSet;
import java.util.Set;

public final class EntityStoreBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private final String entityIndexFile;
  private final Set<String> packagePrefixes;
  private final boolean skipMigrations;

  public EntityStoreBundle(String entityIndexFile, Set<String> packagePrefixes, boolean skipMigrations) {
    this.entityIndexFile = entityIndexFile;
    this.packagePrefixes = packagePrefixes;
    this.skipMigrations = skipMigrations;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    Set<Class<?>> entityClasses = new HashSet<>();
    for (String packagePrefix : packagePrefixes) {
      entityClasses.addAll(MoreReflections.getTypesAnnotatedWith(packagePrefix, EntityKind.class));
    }
    bootstrap.addModule(new EntityStoreModule(entityIndexFile, entityClasses, skipMigrations));
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
  }
}
