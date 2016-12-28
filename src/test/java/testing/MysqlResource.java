package com.davidbyttow.sfe.testing;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.davidbyttow.sfe.storage.DBIs;
import io.dropwizard.db.ManagedDataSource;
import org.flywaydb.core.Flyway;
import org.junit.rules.ExternalResource;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class MysqlResource extends ExternalResource {
  public static final String DEFAULT_MIGRATIONS_DIR = "/db/migration";

  private static final Logger log = LoggerFactory.getLogger(MysqlResource.class);

  private static final Set<String> databasesInitialized = new HashSet<>();
  private static final Supplier<ManagedDataSource> rootDataSource = Suppliers.memoize(() ->
      new DriverManagerDataSource(com.mysql.jdbc.Driver.class.getName(), "jdbc:mysql://localhost?serverTimezone=UTC", "root", ""));

  private final String name;
  private final String migrationsDir;
  private final Supplier<ManagedDataSource> dataSource;

  public MysqlResource(String name) {
    this(name, DEFAULT_MIGRATIONS_DIR);
  }

  public MysqlResource(String name, String migrationsDir) {
    this.name = name;
    this.migrationsDir = migrationsDir;
    DriverManagerDataSource dataSource = new DriverManagerDataSource(
      com.mysql.jdbc.Driver.class.getName(),
      "jdbc:mysql://localhost/" + name + "?serverTimezone=UTC&zeroDateTimeBehavior=convertToNull",
      "root",
      "");
    this.dataSource = Suppliers.memoize(() -> dataSource);
  }

  @Override public void before() {
    try {
      if (databasesInitialized.add(name)) {
        recreateDatabase();
      } else {
        cleanDatabase();
      }
    } catch (SQLException e) {
      throw Throwables.propagate(e);
    }
  }

  public ManagedDataSource dataSource() {
    return dataSource.get();
  }

  public DBI dbi() {
    return DBIs.configure(new DBI(dataSource()));
  }

  @Override public void after() {}

  protected void recreateDatabase() throws SQLException {
    log.info("Recreating database {}", name);

    try (Connection c = rootDataSource.get().getConnection()) {
      try (Statement s = c.createStatement()) {
        s.execute("DROP DATABASE IF EXISTS " + name);
        s.execute("CREATE DATABASE " + name);
      }
    }

    runMigrations();
  }

  protected void runMigrations() {
    // Run migration
    // TODO(matt): Probably don't want to use flyway
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource.get());
    flyway.setLocations(migrationsDir);
    flyway.setBaselineOnMigrate(true);
    int migrationsApplied = flyway.migrate();
    log.info("{} migrations applied.", migrationsApplied);
  }

  private static Set<String> getTables(String databaseName) throws SQLException {
    Set<String> tables = new HashSet<>();
    try (Connection c = rootDataSource.get().getConnection()) {
      try (PreparedStatement ps = c.prepareStatement("SELECT table_name FROM information_schema.TABLES WHERE table_schema = ?")) {
        ps.setString(1, databaseName);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            String tableName = rs.getString(1);
            if (!"schema_version".equalsIgnoreCase(tableName)) {
              tables.add(tableName);
            }
          }
        }
      }
    }

    return tables;
  }

  private void cleanDatabase() throws SQLException {
    log.info("Deleting all rows from {}", name);

    Set<String> tables = getTables(name);
    try (Connection c = dataSource.get().getConnection()) {
      try (Statement s = c.createStatement()) {
        // Disable all foreign keys
        s.execute("SET FOREIGN_KEY_CHECKS=0");

        // Delete all values from all tables
        for (String table : tables) {
          log.info("Deleting from {}", table);
          int deleted = s.executeUpdate("DELETE FROM " + table);
          log.info("Deleted {} rows from {}", deleted, table);
        }

        // Restore foreign keys
        s.execute("SET FOREIGN_KEY_CHECKS=1");
      }
    }
  }
}
