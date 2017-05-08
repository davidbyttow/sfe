package com.simplethingsllc.store.server.index;

import com.google.inject.Inject;
import com.simplethingsllc.store.server.EntityMetadata;
import com.simplethingsllc.store.server.EntityPropField;
import com.simplethingsllc.store.server.driver.SqlExecutor;

public class IndexGenerator {

  private final SqlExecutor sqlExecutor;

  @Inject public IndexGenerator(SqlExecutor sqlExecutor) {
    this.sqlExecutor = sqlExecutor;
  }

  public void maybeGenerateIndexes(EntityMetadata metadata) {
    for (EntityCompositeIndex index : metadata.getCompositeIndexes()) {
      String tableName = CompositeIndexes.getIndexTableName(metadata.getKind(), index.getFieldNames());
      boolean exists = sqlExecutor.query(String.format("SHOW TABLES LIKE '%s'", tableName)).size() > 0;
      if (exists) {
        continue;
      }
      String sql = getCreateIndexTableSql(tableName, index);
      sqlExecutor.update(sql);
    }
    // TODO(d): Backfill here?
  }

  private String getCreateIndexTableSql(String tableName, EntityCompositeIndex index) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append(String.format(
        "CREATE TABLE %s(\n" +
            "entity_id VARCHAR(255) NOT NULL,\n" +
            "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n", tableName));

    for (EntityPropField propField : index.getFields()) {
      sqlBuilder.append(String.format("%s %s NOT NULL,\n", propField.getName(), propField.getType().getColumnType()));
    }

    sqlBuilder.append("PRIMARY KEY (entity_id)) ENGINE=InnoDB DEFAULT CHARSET='utf8';");
    System.out.print(sqlBuilder.toString());
    return sqlBuilder.toString();
  }
}
