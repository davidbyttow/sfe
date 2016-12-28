package com.davidbyttow.sfe.storage;

import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import java.util.List;
import java.util.stream.Collectors;

/** Factory for create {@link DBIReplicaSet}s */
public class DBIReplicaSetFactory extends DBIFactory {
  /**
   * Builds a read-only replica set from a base configuration and list of replica datasources
   * @param env The environment in which we are running
   * @param dsFactory The replicated data source factory, for building the data sources
   * @param nameFormat The format to use for each replica pool name
   * @return The new {@link DBIReplicaSet}
   */
  public DBIReplicaSet buildReadReplicas(Environment env, ReplicatedDataSourceFactory dsFactory, String nameFormat) {
    List<DBI> replicas = dsFactory.buildReadReplicas(env.metrics(), nameFormat)
        .map(ds -> DBIs.configure(build(env, dsFactory, ds, ds.getPoolProperties().getName())))
        .collect(Collectors.toList());

    return new DBIReplicaSet(replicas);
  }
}
