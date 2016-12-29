package io.bold.sfe.common;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Streams {

  public static <T, U> List<U> transform(List<T> from, Function<T, U> mapper) {
    return from.stream().map(mapper).collect(Collectors.toList());
  }

  private Streams() {}
}
