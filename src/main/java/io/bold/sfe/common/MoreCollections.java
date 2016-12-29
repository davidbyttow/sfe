package io.bold.sfe.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class MoreCollections {

  public static <T, U> ArrayList<U> copyTransform(List<T> list, Function<T, U> mapper) {
    ArrayList<U> copy = new ArrayList<>(list.size());
    for (T item : list) {
      copy.add(mapper.apply(item));
    }
    return copy;
  }

  public static <T, U> U getValueAs(Map<T, ?> map, String key, Class<U> type) {
    Object v = map.get(key);
    return (v == null) ? null : type.cast(v);
  }

  public static <T> T getFirst(List<T> list) {
    return (list.isEmpty()) ? null : list.get(0);
  }

  private MoreCollections() {}
}
