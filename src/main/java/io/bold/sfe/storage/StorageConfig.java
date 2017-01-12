package io.bold.sfe.storage;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class StorageConfig {
  @NotNull @NotEmpty String databaseName;
  @NotNull ReplicatedDataSourceFactory database;
  boolean skipMigrations = false;
  String indexFilePath;
}
