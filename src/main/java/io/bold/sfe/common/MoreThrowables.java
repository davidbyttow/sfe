package io.bold.sfe.common;

import com.google.common.base.Throwables;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/**
 * Better throwables support
 */
public final class MoreThrowables {
  /** Propagates an exception, unwrapping the various "wrapped" exceptions */
  public static RuntimeException unwrapAndPropagate(Throwable th) {
    if (th instanceof ExecutionException) {
      return MoreThrowables.unwrapAndPropagate(th.getCause());
    } else if (th instanceof InvocationTargetException) {
      return MoreThrowables.unwrapAndPropagate(((InvocationTargetException) th).getTargetException());
    } else {
      //TODO(d): I'm sure I missed several.
      return Throwables.propagate(th);
    }
  }

  private MoreThrowables() {}
}
