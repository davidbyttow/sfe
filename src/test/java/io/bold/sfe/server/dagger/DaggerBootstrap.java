package io.bold.sfe.server.dagger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;

public class DaggerBootstrap<T extends Configuration> {
  private final ImmutableList.Builder<Class<?>> modules = ImmutableList.builder();
  private final ImmutableList.Builder<Class<?>> requestModules = ImmutableList.builder();
  private final ImmutableList.Builder<DaggerBundle<? super T>> bundles = ImmutableList.builder();
  private final Bootstrap<T> bootstrap;

  DaggerBootstrap(Bootstrap<T> bootstrap) {
    this.bootstrap = Preconditions.checkNotNull(bootstrap);
  }
}
