package io.bold.sfe.storage.entity;

import com.google.common.base.Joiner;
import io.bold.sfe.common.Pair;
import io.bold.sfe.storage.entity.index.CompositeIndexes;
import io.bold.sfe.storage.entity.index.EntityCompositeIndex;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class UpdateGenerator {

  private static final DateTime DEFAULT_DATETIME = new DateTime(0, DateTimeZone.UTC);

  private final EntityMetadata metadata;

  UpdateGenerator(EntityMetadata metadata) {
    this.metadata = metadata;
  }

  String updateIndexedProperties(Object entity, String id, DateTime now) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("INSERT INTO ")
        .append(metadata.getKind())
        .append("_properties (entity_id, prop_key, updated_at, prop_bool, prop_int, prop_long, prop_float, prop_text, prop_datetime, prop_id) VALUES ");

    String updatedAt = DateTimes.toSqlValue(now);
    int i = 0;
    for (EntityPropField propField : metadata.getIndexedFields()) {
      Field field = propField.getField();
      String propKey = propField.getName();
      String valueString;

      Pair<String, Object> formatAndValue = getFormatAndValuePair(entity, propField);
      switch (propField.getType()) {
        case Boolean:
          valueString = String.format("('%s', '%s', '%s', %s, NULL, NULL, NULL, NULL, NULL, NULL)",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        case Integer:
          valueString = String.format("('%s', '%s', '%s', NULL, %s, NULL, NULL, NULL, NULL, NULL)",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        case Long:
          valueString = String.format("('%s', '%s', '%s', NULL, NULL, %s, NULL, NULL, NULL, NULL)",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        case Float:
          valueString = String.format("('%s', '%s', '%s', NULL, NULL, NULL, %s, NULL, NULL, NULL)",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        case String:
          String stringValue = EntityFields.getValueOrDefault(entity, field, "");
          valueString = String.format("('%s', '%s', '%s', NULL, NULL, NULL, NULL, '%s', NULL, NULL)",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        case DateTime:
          valueString = String.format("('%s', '%s', '%s', NULL, NULL, NULL, NULL, NULL, '%s', NULL)",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        case Enum:
          valueString = String.format("('%s', '%s', '%s', NULL, NULL, NULL, NULL, NULL, NULL, '%s')",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        case Id:
          valueString = String.format("('%s', '%s', '%s', NULL, NULL, NULL, NULL, NULL, NULL, '%s')",
              id, propKey, updatedAt, formatAndValue._2);
          break;
        default:
          throw new IllegalStateException("Unknown type: " + field);
      }
      sqlBuilder.append(valueString);
      if (i < metadata.getIndexedFields().size() - 1) {
        sqlBuilder.append(',');
      }
      i++;
    }
    sqlBuilder.append(" ON DUPLICATE KEY UPDATE ")
        .append(String.format("updated_at = '%s', ", updatedAt))
        .append("prop_bool = VALUES(prop_bool), ")
        .append("prop_int = VALUES(prop_int), ")
        .append("prop_long  = VALUES(prop_long), ")
        .append("prop_float = VALUES(prop_float), ")
        .append("prop_text = VALUES(prop_text), ")
        .append("prop_datetime = VALUES(prop_datetime), ")
        .append("prop_id = VALUES(prop_id);");
    return sqlBuilder.toString();
  }

  public String updateCompositeIndex(Object entity, String id, EntityCompositeIndex index, DateTime now) {
    String tableName = CompositeIndexes.getIndexTableName(metadata.getKind(), index.getFieldNames());
    String fieldNamesSql = Joiner.on(", ").join(index.getFieldNames());

    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append(String.format("REPLACE INTO %s (entity_id, updated_at, %s) VALUES ", tableName, fieldNamesSql));

    List<String> propertyUpdates = new ArrayList<>(index.getFields().size());
    for (EntityPropField field : index.getFields()) {
      Pair<String, Object> formatAndValue = getFormatAndValuePair(entity, field);
      propertyUpdates.add(String.format(formatAndValue._1, formatAndValue._2));
    }

    String updatedAt = DateTimes.toSqlValue(now);
    sqlBuilder.append(String.format("('%s', '%s', ", id, updatedAt))
      .append(Joiner.on(", " ).join(propertyUpdates))
      .append(")");

    sqlBuilder.append(";");
    return sqlBuilder.toString();
  }

  private Pair<String, Object> getFormatAndValuePair(Object entity, EntityPropField propField) {
    Field field = propField.getField();
    switch (propField.getType()) {
      case Boolean:
        boolean boolValue = EntityFields.getBoolean(entity, field);
        return Pair.of("%d", boolValue ? 1 : 0);
      case Integer:
        int intValue = EntityFields.getInteger(entity, field);
        return Pair.of("%d", intValue);
      case Long:
        long longValue = EntityFields.getLong(entity, field);
        return Pair.of("%s", Long.toString(longValue));
      case Float:
        float floatValue = EntityFields.getFloat(entity, field);
        return Pair.of("%s", Float.toString(floatValue));
      case String:
        String stringValue = EntityFields.getValueOrDefault(entity, field, "");
        return Pair.of("'%s'", stringValue);
      case DateTime:
        DateTime dateTimeValue = EntityFields.getValueOrDefault(entity, field, DEFAULT_DATETIME);
        return Pair.of("'%s'", DateTimes.toSqlValue(dateTimeValue));
      case Enum:
        String enumName = EntityFields.getEnumName(entity, field);
        return Pair.of("'%s'", enumName);
      case Id:
        String idValue = EntityFields.getValueOrDefault(entity, field, "");
        return Pair.of("'%s'", idValue);
      default:
        throw new IllegalStateException("Unknown type: " + field);
    }
  }
}
