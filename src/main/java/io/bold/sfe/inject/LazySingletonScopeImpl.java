package io.bold.sfe.inject;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;

/**
 * @author d
 */
final class LazySingletonScopeImpl implements Scope {
  @Override
  public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
    return Scopes.SINGLETON.scope(key, unscoped);
  }
}
