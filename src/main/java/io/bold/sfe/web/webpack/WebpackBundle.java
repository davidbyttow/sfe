package io.bold.sfe.web.webpack;

import com.google.inject.Injector;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
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
