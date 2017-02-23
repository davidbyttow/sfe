package io.bold.sfe.cache;

import javax.annotation.Nullable;

public interface CacheLoader<T, U> {
  @Nullable U load(T key);
}
