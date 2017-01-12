package com.simplethingsllc.store.server;

import com.google.common.base.Preconditions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
public @interface Prop {

  enum Type {
    Boolean(true, () -> false, "prop_bool", "TINYINT(1)"),
    Integer(true, () -> 0, "prop_int", "INTEGER"),
    Long(true, () -> 0L, "prop_long", "BIGINT"),
    Float(true, () -> 0.f, "prop_float", "FLOAT"),
    String(true, () -> "", "prop_text", "TEXT"),
    DateTime(false, org.joda.time.DateTime::now, "prop_datetime", "DATETIME"),
    Enum(false, () -> "", "prop_id", "VARCHAR(64)"),
    Id(false, () -> "", "prop_id", "VARCHAR(64)"),
    List(),
    Object();

    private final boolean isNumeric;
    private final Supplier<Object> defaultValueSupplier;
    private final String columnName;
    private final String columnType;

    Type() {
      this.defaultValueSupplier = null;
      this.columnName = null;
      this.columnType = null;
      this.isNumeric = false;
    }

    Type(boolean isNumeric, Supplier<Object> defaultValueSupplier, String columnName, String columnType) {
      this.isNumeric = isNumeric;
      this.defaultValueSupplier = defaultValueSupplier;
      this.columnName = columnName;
      this.columnType = columnType;
    }

    public boolean isWritable() {
      return this.defaultValueSupplier != null;
    }

    public Object defaultValue() {
      Preconditions.checkState(isWritable());
      return defaultValueSupplier.get();
    }

    public String getColumnName() {
      Preconditions.checkState(isWritable());
      return columnName;
    }

    public String getColumnType() {
      Preconditions.checkState(isWritable());
      return columnType;
    }

    public boolean isNumeric() {
      return isNumeric;
    }
  }

  Type value();
}
