package com.simplethingsllc.store.server;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.simplethingsllc.store.common.DateTimes;
import com.simplethingsllc.store.server.index.CompositeIndexes;
import com.simplethingsllc.store.server.index.EntityCompositeIndex;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class UpdateGenerator {

  private static final DateTime DEFAULT_DATETIME = new DateTime(0, DateTimeZone.UTC);

  private final EntityMetadata metadata;

  UpdateGenerator(EntityMetadata metadata) {
    this.metadata = metadata;
  }

  String updateIndexedProperties(Object entity, String id, DateTime now) {
    List<String> statements = new ArrayList<>();
    String updatedAt = DateTimes.toSqlValue(now);
    List<String> rowUpdates = new ArrayList<>();
    for (EntityPropField propField : metadata.getIndexedFields()) {
      String propKey = propField.getName();
      String format = getUpdateValuesFormat(propField.getElementType());

      if (propField.getType() == Prop.Type.List) {
        statements.add(
          String.format("DELETE FROM %s_properties WHERE entity_id = '%s' AND prop_key >= '%s'", metadata.getKind(), id, propKey));

        List<String> values = getFieldValues(entity, propField);
        for (int i = 0; i < values.size(); ++i) {
          String indexedKey = String.format("%s_%d_%d", propKey, i, values.size());
          String valueString = String.format(format, id, indexedKey, updatedAt, values.get(i));
          rowUpdates.add(valueString);
        }
      } else {
        List<String> values = getFieldValues(entity, propField);
        String valueString = String.format(format, id, propKey, updatedAt, values.get(0));
        rowUpdates.add(valueString);
      }
    }

    StringBuilder b = new StringBuilder();
    b.append("INSERT INTO ")
      .append(metadata.getKind())
      .append("_properties (entity_id, prop_key, updated_at, prop_bool, prop_int, prop_long, prop_float, prop_text, prop_datetime, prop_id) VALUES ");
    Joiner.on(",").appendTo(b, rowUpdates);
    b.append(" ON DUPLICATE KEY UPDATE ")
        .append(String.format("updated_at = '%s', ", updatedAt))
        .append("prop_bool = VALUES(prop_bool), ")
        .append("prop_int = VALUES(prop_int), ")
        .append("prop_long  = VALUES(prop_long), ")
        .append("prop_float = VALUES(prop_float), ")
        .append("prop_text = VALUES(prop_text), ")
        .append("prop_datetime = VALUES(prop_datetime), ")
        .append("prop_id = VALUES(prop_id);");
    statements.add(b.toString());

    return Joiner.on(";").join(statements);
  }

  public String updateCompositeIndex(Object entity, String id, EntityCompositeIndex index, DateTime now) {
    String tableName = CompositeIndexes.getIndexTableName(metadata.getKind(), index.getFieldNames());
    String fieldNamesSql = Joiner.on(", ").join(index.getFieldNames());

    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append(String.format("REPLACE INTO %s (entity_id, updated_at, %s) VALUES ", tableName, fieldNamesSql));

    List<String> propertyUpdates = new ArrayList<>(index.getFields().size());
    for (EntityPropField field : index.getFields()) {
      List<String> values = getFieldValues(entity, field);
      for (String value : values) {
        propertyUpdates.add(String.format("'%s'", value));
      }
    }

    String updatedAt = DateTimes.toSqlValue(now);
    sqlBuilder.append(String.format("('%s', '%s', ", id, updatedAt))
      .append(Joiner.on(", " ).join(propertyUpdates))
      .append(")");

    sqlBuilder.append(";");
    return sqlBuilder.toString();
  }

  private List<String> getFieldValues(Object entity, EntityPropField propField) {
    Field field = propField.getField();
    if (propField.getType() == Prop.Type.List) {
      switch (propField.getElementType()) {
        case Boolean:
          return getFieldElementValues(entity, field, Boolean.class, v -> v ? "1" : "0");
        case Integer:
          return getFieldElementValues(entity, field, Integer.class, v -> Integer.toString(v));
        case Long:
          return getFieldElementValues(entity, field, Long.class, v -> Long.toString(v));
        case Float:
          return getFieldElementValues(entity, field, Float.class, v -> Float.toString(v));
        case DateTime:
          return getFieldElementValues(entity, field, DateTime.class, DateTimes::toSqlValue);
        case Enum:
          return getFieldElementValues(entity, field, Enum.class, Enum::name);
        case String:
        case Id:
          return getFieldElementValues(entity, field, String.class,  Strings::nullToEmpty);
        default:
          throw new IllegalStateException("Unknown type: " + field);
      }
    } else {
      return ImmutableList.of(getFieldValue(entity, propField.getField(), propField.getType()));
    }
  }

  private String getFieldValue(Object entity, Field field, Prop.Type propType) {
    Preconditions.checkArgument(propType.isWritable());
    switch (propType) {
      case Boolean:
        return EntityFields.getBoolean(entity, field) ? "1" : "0";
      case Integer:
        return Integer.toString(EntityFields.getInteger(entity, field));
      case Long:
        return Long.toString(EntityFields.getLong(entity, field));
      case Float:
        return Float.toString(EntityFields.getFloat(entity, field));
      case String:
        return EntityFields.getValueOrDefault(entity, field, "");
      case DateTime:
        return DateTimes.toSqlValue(EntityFields.getValueOrDefault(entity, field, DEFAULT_DATETIME));
      case Enum:
        return EntityFields.getEnumName(entity, field);
      case Id:
        return EntityFields.getValueOrDefault(entity, field, "");
      default:
        throw new IllegalStateException("Unknown type: " + field);
    }
  }

  private String getUpdateValuesFormat(Prop.Type type) {
    switch (type) {
      case Boolean:
        return "('%s', '%s', '%s', %s, NULL, NULL, NULL, NULL, NULL, NULL)";
      case Integer:
        return "('%s', '%s', '%s', NULL, %s, NULL, NULL, NULL, NULL, NULL)";
      case Long:
        return "('%s', '%s', '%s', NULL, NULL, %s, NULL, NULL, NULL, NULL)";
      case Float:
        return "('%s', '%s', '%s', NULL, NULL, NULL, %s, NULL, NULL, NULL)";
      case String:
        return "('%s', '%s', '%s', NULL, NULL, NULL, NULL, '%s', NULL, NULL)";
      case DateTime:
        return "('%s', '%s', '%s', NULL, NULL, NULL, NULL, NULL, '%s', NULL)";
      case Enum:
        return "('%s', '%s', '%s', NULL, NULL, NULL, NULL, NULL, NULL, '%s')";
      case Id:
        return "('%s', '%s', '%s', NULL, NULL, NULL, NULL, NULL, NULL, '%s')";
      default:
        throw new IllegalStateException("Unknown type: " + type);
    }
  }

  private <T> List<String> getFieldElementValues(Object entity, Field field, Class<T> type, Function<T, String> mapper) {
    return EntityFields.getValues(entity, field, type).stream()
      .map(mapper)
      .collect(Collectors.toList());
  }
}
