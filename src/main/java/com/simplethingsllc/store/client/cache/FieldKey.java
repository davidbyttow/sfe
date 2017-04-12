package com.simplethingsllc.store.client.cache;

import java.util.function.Function;

public class FieldKey<T> {
  private final String name;
  private final Function<T, String> extractor;

  public static <T> FieldKey<T> of(String name, Function<T, String> extractor) {
    return new FieldKey<>(name, extractor);
  }

  private FieldKey(String name, Function<T, String> extractor) {
    this.name = name;
    this.extractor = extractor;
  }

  public String getName() {
    return name;
  }

  public Function<T, String> getExtractor() {
    return extractor;
  }
}
