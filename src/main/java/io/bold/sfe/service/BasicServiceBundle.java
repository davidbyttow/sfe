package io.bold.sfe.service;

import com.google.inject.Injector;
import com.google.inject.Provides;
import io.bold.sfe.async.TaskProcessor;
import io.bold.sfe.cache.CacheBundle;
import io.bold.sfe.common.ProviderOnlyModule;
import io.bold.sfe.concurrent.BackgroundThreadPoolBundle;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.config.CommonConfigBundle;
import io.bold.sfe.environment.aws.AwsEnvBundle;
import io.bold.sfe.environment.aws.AwsInstanceMetadata;
import io.bold.sfe.environment.local.LocalEnvBundle;
import io.bold.sfe.health.ServerStatusBundle;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.inject.LazySingleton;
import io.bold.sfe.json.JsonBundle;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Common bundle included by all services.  Configures standard logging, metrics, authn, caching, etc */
public class BasicServiceBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private static final Logger logger = LoggerFactory.getLogger(BasicServiceBundle.class);

  private static final boolean FORCE_AWS = false;

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new EnvModule());
    bootstrap.addBundle(new ServerStatusBundle());

    bootstrap.addModule(new ProviderOnlyModule() {
      @Provides
      @LazySingleton
      HttpClient httpClient(Environment env) {
        HttpClientConfiguration config = new HttpClientConfiguration();
        config.setTimeout(Duration.seconds(10));
        config.setConnectionTimeout(Duration.seconds(10));
        return new HttpClientBuilder(env).using(config).build("global");
      }
    });

    @SuppressWarnings("unchecked")
    GuiceBootstrap<T> typedBootstrap = (GuiceBootstrap<T>) bootstrap;
    typedBootstrap.addBundle(new BackgroundThreadPoolBundle<>());
    typedBootstrap.addBundle(new CommonConfigBundle<>());
    typedBootstrap.addBundle(new JsonBundle<>());
    typedBootstrap.addBundle(new CacheBundle<>());

    if (FORCE_AWS || AwsInstanceMetadata.isAvailable()) {
      typedBootstrap.addBundle(new AwsEnvBundle<>());
    } else {
      typedBootstrap.addBundle(new LocalEnvBundle<>());
    }

    // TODO(d): Add logging bundle
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    TaskProcessor taskProcessor = injector.getInstance(TaskProcessor.class);
    environment.lifecycle().manage(taskProcessor);
  }
}
