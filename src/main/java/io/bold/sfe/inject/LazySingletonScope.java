package io.bold.sfe.inject;

import com.google.inject.Scope;

/**
 * A singleton factory that returns a Guice {@link Scope} that enables lazy singletons
 */
public class LazySingletonScope {
  /**
   * Returns the scope
   * @return scope
   */
  public static Scope get() {
    return instance;
  }

  private static final Scope instance = new LazySingletonScopeImpl();

  private LazySingletonScope() {}
}
