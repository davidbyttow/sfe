package io.bold.sfe.storage;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.LazySingleton;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.util.Set;

public final class StorageModule extends AbstractModule {

  private final String databaseName;
  private final Set<Class<?>> storageProviderClasses;

  public StorageModule(String databaseName, Set<Class<?>> storageProviderClasses) {
    this.databaseName = databaseName;
    this.storageProviderClasses = storageProviderClasses;
  }

  protected void configure() {
    for (Class<?> storageClass : storageProviderClasses) {
      install(new DbModule<>(storageClass));
    }
  }

  static class MasterProvider<T> implements Provider<T> {
    @Inject @ForWrites private Provider<DBI> dbi;

    private final Class<T> storageType;

    MasterProvider(Class<T> storageType) {
      this.storageType = storageType;
    }

    @Override public T get() {
      return dbi.get().onDemand(storageType);
    }
  }

  static class ReplicaProvider<T> implements Provider<T> {
    @Inject @ForReads private Provider<DBIReplicaSet> replicas;

    private final Class<T> storageType;

    ReplicaProvider(Class<T> storageType) {
      this.storageType = storageType;
    }

    @Override public T get() {
      return replicas.get().pickRandom().onDemand(storageType);
    }
  }

  static class DbModule<T> extends AbstractModule {
    private final Class<T> storageType;

    DbModule(Class<T> storageType) {
      this.storageType = storageType;
    }

    @Override protected void configure() {
      bind(storageType).annotatedWith(ForWrites.class).toProvider(new MasterProvider<>(storageType));
      //bind(storageType).annotatedWith(ForReads.class).toProvider(new ReplicaProvider<>(storageType));
    }
  }

  @Provides @LazySingleton DataSourceManager provideDataSourceManager(
      Environment env, BasicServiceConfig config, MetricRegistry metrics) {
    DataSourceManager dm = new DataSourceManager(env, config, metrics);
    dm.init(databaseName);
    return dm;
  }

  @Provides @ForWrites DataSource provideDataSource(DataSourceManager dataSourceManager) {
    return dataSourceManager.getDataSource();
  }

  @Provides @ForWrites DBI writeDBI(DataSourceManager dataSourceManager) throws ClassNotFoundException {
    return dataSourceManager.getWriteDbi();
  }

  @Provides @ForReads DBIReplicaSet readReplicas(DataSourceManager dataSourceManager) {
    return dataSourceManager.getReplicaSet();
  }
}
