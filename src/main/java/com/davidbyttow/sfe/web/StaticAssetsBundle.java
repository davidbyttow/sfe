package com.davidbyttow.sfe.web;

import com.google.inject.Injector;
import com.davidbyttow.sfe.common.Paths;
import com.davidbyttow.sfe.environment.ResourceLoader;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import com.davidbyttow.sfe.service.Env;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class StaticAssetsBundle<T extends Configuration> implements ConfiguredGuiceBundle<T> {

  private final String assetsPath;
  private final String uriPath;

  public StaticAssetsBundle(String uriPath, String assetsPath) {
    this.assetsPath = Paths.withTrailingSlash(assetsPath);
    this.uriPath = Paths.withTrailingSlash(uriPath);
  }

  @Override
  public void initialize(GuiceBootstrap<?> bootstrap) {}

  @Override
  public void run(Injector injector, T configuration, Environment environment) throws Exception {
    Env env = injector.getInstance(Env.class);
    ResourceLoader resourceLoader = injector.getInstance(ResourceLoader.class);
    environment.servlets().addServlet(uriPath, new StaticAssetsServlet(assetsPath, uriPath, resourceLoader, env)).addMapping(uriPath + "*");
  }
}
