package com.davidbyttow.sfe.web.webpack;

import com.google.inject.Injector;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WebpackBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {
  private static final Logger log = LoggerFactory.getLogger(WebpackBundle.class);

  private final String manifestFile;

  public WebpackBundle(String manifestFile) {
    this.manifestFile = manifestFile;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new WebpackModule(manifestFile));
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
  }
}
