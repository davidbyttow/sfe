package com.simplethingsllc.store.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ClientConfig {
  public List<CompositeIndexDef> indexes = new ArrayList<>();
  public Set<Class<?>> entityTypes = new HashSet<>();
  public ObjectMapper objectMapper;
  public ExecutorService executorService;

  // TODO(d): Entity store should make its own connections or be given a thin interface to create data sources.
  public DataSource dataSource;
  public DBI dbi;

  public boolean skipMigrations;
}
