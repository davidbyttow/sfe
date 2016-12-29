package io.bold.sfe.storage.entity;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Query<T> {

  private static final int DEFAULT_LIMIT = 1000;

  private final Class<T> type;
  private final List<Filter> filters;
  private final Ordering ordering;
  private final int limit;
  private final Set<String> properties;

  private Query(Class<T> type, List<Filter> filters, Ordering ordering, int limit) {
    this.type = type;
    this.filters = filters;
    this.ordering = ordering;
    this.limit = limit;

    this.properties = new HashSet<>();
    for (Filter filter : filters) {
      properties.add(filter.getProperty());
    }
    if (ordering != null) {
      properties.add(ordering.getProperty());
    }
  }

  public static <T> Builder<T> newBuilder(Class<T> type) {
    Preconditions.checkArgument(type.getAnnotation(EntityKind.class) != null);
    return new Builder<>(type);
  }

  public Class<T> getType() {
    return type;
  }

  public List<Filter> getFilters() {
    return filters;
  }

  public Ordering getOrdering() {
    return ordering;
  }

  public int getLimit() {
    return limit;
  }

  public Set<String> getProperties() {
    return properties;
  }

  public enum Equality {
    Equals("="),
    NotEquals("!="),
    LessThan("<"),
    LessThanOrEqualTo("<="),
    GreaterThan(">"),
    GreaterThanOrEqualTo(">=");

    private final String symbol;

    Equality(String symbol) {
      this.symbol = symbol;
    }

    public String symbol() {
      return symbol;
    }
  }

  public static class Filter {
    private final String property;
    private final Equality equality;
    private final Object value;

    private Filter(String property, Equality equality, Object value) {
      this.property = property;
      this.equality = equality;
      this.value = value;
    }

    public String getProperty() {
      return property;
    }

    public Equality getEquality() {
      return equality;
    }

    public Object getValue() {
      return value;
    }
  }

  public static class Ordering {
    private final String property;
    private final boolean descending;

    private Ordering(String property, boolean descending) {
      this.property = property;
      this.descending = descending;
    }

    public String getProperty() {
      return property;
    }

    public boolean isDescending() {
      return descending;
    }
  }

  public static class Builder<T> {
    private final Class<T> type;
    private List<Filter> filters = new ArrayList<>();
    private Ordering ordering = null;
    private int limit = DEFAULT_LIMIT;

    private Builder(Class<T> type) {
      this.type = type;
    }

    private void checkField(String fieldName) {
      try {
        Field field = type.getDeclaredField(fieldName);
        if (field.getAnnotation(Indexed.class) == null) {
          throw new IllegalArgumentException("Field is not indexed: " + fieldName);
        }
      } catch (NoSuchFieldException e) {
        throw Throwables.propagate(e);
      }
    }

    public Builder<T> addFilter(String property, Equality equality, Object value) {
      checkField(property);
      Filter f = new Filter(property, equality, value);
      filters.add(f);
      return this;
    }

    public Builder<T> orderBy(String property) {
      ordering = new Ordering(property, false);
      return this;
    }

    public Builder<T> orderByDescending(String property) {
      ordering = new Ordering(property, true);
      return this;
    }

    public Builder<T> limit(int limit) {
      this.limit = limit;
      return this;
    }

    public Query<T> build() {
      return new Query<>(type, filters, ordering, limit);
    }
  }
}
