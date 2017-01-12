package com.simplethingsllc.store.server.driver;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class StoreInitializer {

  public static void runMigrations(DataSource dataSource) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.setLocations("/db/migration");
    flyway.setBaselineOnMigrate(true);
    flyway.setBaselineVersionAsString("0");
    flyway.migrate();
  }
}
