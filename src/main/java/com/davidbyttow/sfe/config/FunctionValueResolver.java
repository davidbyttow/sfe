package com.davidbyttow.sfe.config;

import com.github.jknack.handlebars.ValueResolver;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class FunctionValueResolver implements ValueResolver {
  @Override public Object resolve(Object context, String name) {
    if (!(context instanceof Function)) {
      return UNRESOLVED;
    }

    @SuppressWarnings("unchecked")
    Function<String, Object> f = (Function<String, Object>) context;
    Object value = f.apply(name);
    return value == null ? UNRESOLVED : value;
  }

  @Override public Object resolve(Object context) {
    return context == null ? UNRESOLVED : context;
  }

  @Override public Set<Map.Entry<String, Object>> propertySet(Object context) {
    return ImmutableSet.of();
  }
}
