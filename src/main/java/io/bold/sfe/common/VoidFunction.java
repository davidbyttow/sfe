package io.bold.sfe.common;

import javax.annotation.Nullable;

public interface VoidFunction<T> {
  void apply(@Nullable T input);

  @Override
  boolean equals(@Nullable Object object);
}
