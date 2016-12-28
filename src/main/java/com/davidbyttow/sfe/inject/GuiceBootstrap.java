package com.davidbyttow.sfe.inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.cli.Command;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;

import java.util.List;

/** Replacement for the Dropwizard {@link Bootstrap} which also allows initializing Guice related resources */
public class GuiceBootstrap<T extends Configuration> {
  private final ImmutableList.Builder<Module> modules = ImmutableList.builder();
  private final ImmutableList.Builder<ConfiguredGuiceBundle<? super T>> guiceBundles = ImmutableList.builder();
  private final Bootstrap<T> bootstrap;

  GuiceBootstrap(Bootstrap<T> bootstrap) {
    this.bootstrap = Preconditions.checkNotNull(bootstrap);
  }

  public Bootstrap<T> dropwizard() {
    return bootstrap;
  }

  public void addBundle(Bundle bundle) {
    bootstrap.addBundle(bundle);
  }

  public void addBundle(ConfiguredBundle<? super T> bundle) {
    bootstrap.addBundle(bundle);
  }

  public void addBundle(ConfiguredGuiceBundle<? super T> bundle) {
    bundle.initialize(this);
    guiceBundles.add(bundle);
  }

  public void addModule(Module module) {
    modules.add(module);
  }

  public List<Module> getModules() {
    return modules.build();
  }

  public List<ConfiguredGuiceBundle<? super T>> getGuiceBundles() {
    return guiceBundles.build();
  }

  public void addCommand(Command command) {
    bootstrap.addCommand(command);
  }

  public void addCommand(ConfiguredCommand<? super T> command) {
    bootstrap.addCommand(command);
  }
}
