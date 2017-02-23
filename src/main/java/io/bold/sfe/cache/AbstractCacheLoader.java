package io.bold.sfe.cache;

import com.google.common.util.concurrent.Futures;

import javax.annotation.Nullable;

public abstract class AbstractCacheLoader<T, U> implements AsyncCacheLoader<T,U> {
  @Override @Nullable public U load(T key) {
    return Futures.getUnchecked(loadAsync(key));
  }
}
