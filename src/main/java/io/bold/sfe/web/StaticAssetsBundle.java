package io.bold.sfe.web;

import com.google.inject.Injector;
import io.bold.sfe.common.Paths;
import io.bold.sfe.environment.ResourceLoader;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.service.Env;
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
