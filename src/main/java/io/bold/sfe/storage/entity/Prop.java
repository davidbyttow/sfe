package io.bold.sfe.storage.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {

  enum Type {
    Boolean(() -> false, "prop_bool", "TINYINT(1)"),
    Integer(() -> 0, "prop_int", "INTEGER"),
    Long(() -> 0L, "prop_long", "BIGINT"),
    Float(() -> 0.f, "prop_float", "FLOAT"),
    String(() -> "", "prop_text", "TEXT"),
    DateTime(org.joda.time.DateTime::now, "prop_datetime", "DATETIME"),
    Enum(() -> "", "prop_id", "VARCHAR(64)"),
    Id(() -> "", "prop_id", "VARCHAR(64)"),
    Object(() -> null, "", "NULL");

    private final Supplier<Object> defaultValueSupplier;
    private final String columnName;
    private final String columnType;

    Type(Supplier<Object> defaultValueSupplier, String columnName, String columnType) {
      this.defaultValueSupplier = defaultValueSupplier;
      this.columnName = columnName;
      this.columnType = columnType;
    }

    public Object defaultValue() {
      return defaultValueSupplier.get();
    }

    public String getColumnName() {
      return columnName;
    }

    public String getColumnType() {
      return columnType;
    }
  }

  Type value();
}
