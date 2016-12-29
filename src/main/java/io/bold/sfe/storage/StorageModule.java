package io.bold.sfe.storage;

import com.codahale.metrics.MetricRegistry;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.LazySingleton;
import io.bold.sfe.storage.entity.EntitiesStorage;
import io.bold.sfe.storage.entity.EntityModule;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public final class StorageModule extends AbstractModule {

  private static final String INDEX_FILE_PATH = "/db/indexes.json";

  private final String databaseName;
  private final String packagePrefix;

  public StorageModule(String databaseName, String packagePrefix) {
    this.databaseName = databaseName;
    this.packagePrefix = packagePrefix;
  }

  protected void configure() {
    install(new EntityModule(packagePrefix, INDEX_FILE_PATH));
    install(new DbModule<>(EntitiesStorage.class));

    for (Class<?> storageClass : MoreReflections.getTypesAnnotatedWith(packagePrefix, StorageProvider.class)) {
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
