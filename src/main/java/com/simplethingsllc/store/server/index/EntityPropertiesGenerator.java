package com.simplethingsllc.store.server.index;

import com.google.inject.Inject;
import com.simplethingsllc.store.server.EntityMetadata;
import com.simplethingsllc.store.server.driver.SqlExecutor;

public class EntityPropertiesGenerator {

  private static final String PROPERTIES_TABLE_FORMAT =
      "CREATE TABLE %s_properties(\n" +
          "  entity_id       VARCHAR(255) NOT NULL,\n" +
          "  prop_key        VARCHAR(128) NOT NULL,\n" +
          "  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
          "  prop_bool       TINYINT(1),\n" +
          "  prop_int        INTEGER,\n" +
          "  prop_long       BIGINT,\n" +
          "  prop_float      FLOAT,\n" +
          "  prop_text       MEDIUMTEXT,\n" +
          "  prop_datetime   DATETIME,\n" +
          "  prop_id         VARCHAR(255),\n" +
          "  PRIMARY KEY(entity_id, prop_key)\n" +
          ") ENGINE=InnoDB DEFAULT CHARSET='utf8';\n";

  private final SqlExecutor sqlExecutor;

  @Inject public EntityPropertiesGenerator(SqlExecutor sqlExecutor) {
    this.sqlExecutor = sqlExecutor;
  }

  public void maybeGenerateProperties(EntityMetadata metadata) {
    String tableName = String.format("%s_properties", metadata.getKind());
    boolean exists = sqlExecutor.query(String.format("SHOW TABLES LIKE '%s'", tableName)).size() > 0;
    if (exists) {
      return;
    }
    String sql = String.format(PROPERTIES_TABLE_FORMAT, metadata.getKind());
    sqlExecutor.update(sql);
  }
}
