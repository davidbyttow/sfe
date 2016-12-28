package com.davidbyttow.sfe.storage;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.davidbyttow.sfe.common.MoreStreams;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedPooledDataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class ReplicatedDataSourceFactory extends DataSourceFactory {
  @JsonProperty private List<String> readReplicas = ImmutableList.of();

  public List<String> getReadReplicas() {
    return readReplicas;
  }

  public void setReadReplicas(List<String> readReplicas) {
    this.readReplicas = readReplicas;
  }

  public Stream<ManagedPooledDataSource> buildReadReplicas(MetricRegistry metrics, String nameFormat) {
    return MoreStreams.zipWithIndex(readReplicas.stream()).map((rr) ->
        buildReplica(metrics, rr._1, String.format(nameFormat, rr._2)));
  }

  private ManagedPooledDataSource buildReplica(MetricRegistry metrics, String url, String name) {
    final Properties properties = new Properties();
    for (Map.Entry<String, String> property : getProperties().entrySet()) {
      properties.setProperty(property.getKey(), property.getValue());
    }

    final PoolProperties poolConfig = new PoolProperties();
    poolConfig.setAbandonWhenPercentageFull(getAbandonWhenPercentageFull());
    poolConfig.setAlternateUsernameAllowed(isAlternateUsernamesAllowed());
    poolConfig.setCommitOnReturn(getCommitOnReturn());
    poolConfig.setDbProperties(properties);
    poolConfig.setDefaultAutoCommit(getAutoCommitByDefault());
    poolConfig.setDefaultCatalog(getDefaultCatalog());
    poolConfig.setDefaultReadOnly(true);
    poolConfig.setDefaultTransactionIsolation(getDefaultTransactionIsolation().get());
    poolConfig.setDriverClassName(getDriverClass());
    poolConfig.setFairQueue(getUseFairQueue());
    poolConfig.setInitialSize(getInitialSize());
    poolConfig.setInitSQL(getInitializationQuery());
    poolConfig.setLogAbandoned(getLogAbandonedConnections());
    poolConfig.setLogValidationErrors(getLogValidationErrors());
    poolConfig.setMaxActive(getMaxSize());
    poolConfig.setMaxIdle(getMaxSize());
    poolConfig.setMinIdle(getMinSize());

    if (getMaxConnectionAge().isPresent()) {
      poolConfig.setMaxAge(getMaxConnectionAge().get().toMilliseconds());
    }

    poolConfig.setMaxWait((int) getMaxWaitForConnection().toMilliseconds());
    poolConfig.setMinEvictableIdleTimeMillis((int) getMinIdleTime().toMilliseconds());
    poolConfig.setName(name);
    poolConfig.setUrl(url); // NB(matt): Not getUrl
    poolConfig.setUsername(getUser());
    poolConfig.setPassword(getPassword());
    poolConfig.setTestWhileIdle(getCheckConnectionWhileIdle());
    poolConfig.setValidationQuery(getValidationQuery());
    poolConfig.setTestOnBorrow(getCheckConnectionOnBorrow());
    poolConfig.setTestOnConnect(getCheckConnectionOnConnect());
    poolConfig.setTestOnReturn(getCheckConnectionOnReturn());
    poolConfig.setTimeBetweenEvictionRunsMillis((int) getEvictionInterval().toMilliseconds());
    poolConfig.setValidationInterval(getValidationInterval().toMilliseconds());
    return new ManagedPooledDataSource(poolConfig, metrics);
  }
}
