package io.bold.sfe.storage.entity;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import io.bold.sfe.common.Streams;
import io.bold.sfe.storage.entity.index.CompositeIndexes;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class QueryGenerator {

  private final EntityMetadata metadata;

  QueryGenerator(EntityMetadata metadata) {
    this.metadata = metadata;
  }

  String singleFilter(List<Query.Filter> filters, int limit) {
    Map<String, EntityPropField> indexedFieldMap = metadata.getIndexedFieldMap();
    Query.Filter firstFilter = filters.get(0);
    EntityPropField propField = indexedFieldMap.get(firstFilter.getProperty());
    StringBuilder filterBuilder = new StringBuilder();
    for (Query.Filter filter : filters) {
      if (filterBuilder.length() > 0) {
        filterBuilder.append(" AND ");
      }
      filterBuilder.append(String.format("%s %s %s",
        propField.getType().getColumnName(),
        filter.getEquality().symbol(),
        valueToSqlConstant(propField, filter.getValue())));
    }

    StringBuilder sqlBuilder = newPropertyQuery()
        .append(String.format(" WHERE (prop_key = '%s' AND %s) ORDER BY entity_id",
            firstFilter.getProperty(),
            filterBuilder.toString()))
        .append(" LIMIT ").append(limit).append(';');

    return sqlBuilder.toString();
  }

  String singleFilterOrdered(Query.Filter filter, boolean descending, int limit) {
    Map<String, EntityPropField> indexedFieldMap = metadata.getIndexedFieldMap();
    EntityPropField propField = indexedFieldMap.get(filter.getProperty());

    String columnName = propField.getType().getColumnName();
    StringBuilder sqlBuilder = newPropertyQuery()
      .append(String.format(" WHERE (prop_key = '%s' AND %s %s %s) ORDER BY %s",
        filter.getProperty(),
        columnName,
        filter.getEquality().symbol(),
        valueToSqlConstant(propField, filter.getValue()),
        columnName));
    if (descending) {
      sqlBuilder.append(" DESC");
    }
    sqlBuilder.append(" LIMIT ").append(limit).append(';');

    return sqlBuilder.toString();
  }

  String orderedBy(Query.Ordering ordering, int limit) {
    Map<String, EntityPropField> indexedFieldMap = metadata.getIndexedFieldMap();
    EntityPropField propField = indexedFieldMap.get(ordering.getProperty());
    Preconditions.checkArgument(propField != null);
    StringBuilder sqlBuilder = newPropertyQuery()
        .append(String.format(" WHERE (prop_key = '%s') ORDER BY %s",
            ordering.getProperty(),
            propField.getType().getColumnName()));
    if (ordering.isDescending()) {
      sqlBuilder.append(" DESC");
    }
    sqlBuilder.append(" LIMIT ").append(limit).append(';');
    return sqlBuilder.toString();
  }

  String multiFilter(List<Query.Filter> filters, int limit) {
    return createMultiFilteredSql(filters, null, limit);
  }

  String multiFilterOrdered(List<Query.Filter> filters, Query.Ordering ordering, int limit) {
    return createMultiFilteredSql(filters, ordering, limit);
  }

  private String createMultiFilteredSql(List<Query.Filter> filters, @Nullable Query.Ordering ordering, int limit) {
    List<String> filterNames = Streams.transform(filters, Query.Filter::getProperty);
    if (ordering != null) {
      filterNames.add(ordering.getProperty());
    }
    String tableName = CompositeIndexes.getIndexTableName(metadata.getKind(), filterNames);
    StringBuilder sqlBuilder = newIndexQuery(tableName);

    sqlBuilder.append(" WHERE ");
    List<String> propertyQueries = new ArrayList<>(filters.size());
    Map<String, EntityPropField> indexedFieldMap = metadata.getIndexedFieldMap();
    for (Query.Filter filter : filters) {
      EntityPropField propField = indexedFieldMap.get(filter.getProperty());
      propertyQueries.add(String.format("%s %s %s",
          filter.getProperty(),
          filter.getEquality().symbol(), valueToSqlConstant(propField, filter.getValue())));
    }

    sqlBuilder.append(Joiner.on(" AND " ).join(propertyQueries));

    if (ordering != null) {
      sqlBuilder.append(String.format(" ORDER BY %s", ordering.getProperty()));
      if (ordering.isDescending()) {
        sqlBuilder.append(" DESC");
      }
    }

    sqlBuilder.append(" LIMIT ").append(limit).append(';');
    return sqlBuilder.toString();
  }

  private StringBuilder newKindQuery() {
    return new StringBuilder(String.format("SELECT * FROM entities WHERE kind='%s'", metadata.getKind()));
  }

  private StringBuilder newPropertyQuery() {
    return new StringBuilder(String.format("SELECT entity_id, prop_key FROM %s_properties", metadata.getKind()));
  }

  private StringBuilder newIndexQuery(String tableName) {
    return new StringBuilder(String.format("SELECT entity_id FROM %s", tableName));
  }

  private String valueToSqlConstant(EntityPropField propField, Object value) {
    switch (propField.getType()) {
      case Boolean:
        return ((boolean) value) ? "1" : "0";
      case Integer:
        return Integer.toString((int) value);
      case Long:
        return Long.toString((long) value);
      case Float:
        return Float.toString((float) value);
      case String:
      case Enum:
      case Id:
        return "'" + value + "'";
      case DateTime:
        DateTime dateTime = (DateTime) value;
        return "'" + DateTimes.toSqlValue(dateTime) + "'";
      default:
        throw new IllegalStateException("Unknown type: " + propField);
    }
  }
}
