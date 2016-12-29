package io.bold.sfe.storage;

import com.codahale.metrics.MetricRegistry;
import io.bold.sfe.config.BasicServiceConfig;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class DataSourceManager {

  private final Environment env;
  private final BasicServiceConfig config;
  private final MetricRegistry metrics;

  private DataSource dataSource;
  private DBI writeDbi;
  private DBIReplicaSet replicaSet;
  private String databaseName;

  DataSourceManager(Environment env, BasicServiceConfig config, MetricRegistry metrics) {
    this.env = env;
    this.config = config;
    this.metrics = metrics;
  }

  void init(String databaseName) {
    if (this.databaseName != null) {
      throw new IllegalStateException("Datasource already initialized, did you want to reload?");
    }
    this.databaseName = databaseName;
    setupConnections();
  }

  public void reload() {
    if (this.databaseName == null) {
      throw new IllegalStateException("Datasource was never initialized");
    }
    setupConnections();
  }

  private void setupConnections() {
    dataSource = config.database.build(metrics, databaseName);

    DBI dbi = new DBIFactory().build(env, config.database, databaseName);
    writeDbi = DBIs.configure(dbi);

    DBIReplicaSetFactory dbiFactory = new DBIReplicaSetFactory();
    replicaSet = dbiFactory.buildReadReplicas(env, config.database, databaseName + "-replica-%d");
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public DBI getWriteDbi() {
    return writeDbi;
  }

  public DBIReplicaSet getReplicaSet() {
    return replicaSet;
  }
}
