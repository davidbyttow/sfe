package com.davidbyttow.sfe.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.List;

/**
 */
public abstract class GuiceApplication<T extends Configuration> extends Application<T> {

  private GuiceBootstrap<T> guiceBootstrap;
  private GuiceBundle guiceBundle;
  private DropwizardEnvironmentModule environmentModule;

  /**
   * Initializes the application bootstrap, adding any bundles or modules required by the application
   *
   * @param bootstrap The application bootstrap
   */
  public abstract void initialize(GuiceBootstrap<T> bootstrap);

  /**
   * Initializes the application environment
   *
   * @param injector the injector for the application
   * @param configuration the application's configuration
   * @param environment the application environment
   */
  public abstract void run(Injector injector, T configuration, Environment environment);

  @Override public final void initialize(Bootstrap<T> bootstrap) {
    this.guiceBootstrap = new GuiceBootstrap<>(bootstrap);

    initialize(guiceBootstrap);

    environmentModule = new DropwizardEnvironmentModule<>(getConfigurationClass());

    ImmutableList.Builder<Module> modules = ImmutableList.builder();
    modules.add(environmentModule);
    modules.addAll(guiceBootstrap.getModules());
    modules.add(new AbstractModule() {
      @Override protected void configure() {
        bindScope(LazySingleton.class, LazySingletonScope.get());
      }
    });

    guiceBundle = GuiceBundle.<T>newBuilder()
        .addModule(Modules.combine(modules.build()))
        .setConfigClass(getConfigurationClass())
        .build();
    guiceBootstrap.dropwizard().addBundle(guiceBundle);
  }

  @Override public final void run(T configuration, Environment environment) throws Exception {
    Injector injector = guiceBundle.getInjector();

    environmentModule.setEnvironment(environment);
    environmentModule.setConfiguration(configuration);

    List<ConfiguredGuiceBundle<? super T>> guiceBundles = guiceBootstrap.getGuiceBundles();
    for (ConfiguredGuiceBundle<? super T> guiceBundle : guiceBundles) {
      guiceBundle.run(injector, configuration, environment);
    }

    guiceBootstrap = null;

    run(injector, configuration, environment);
  }
}
