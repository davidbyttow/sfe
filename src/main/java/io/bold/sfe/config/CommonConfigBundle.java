package io.bold.sfe.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.service.Env;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Installs a custom configuration source provider that allows for parameterization of config based on environment */
public final class CommonConfigBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {
  private static final Logger logger = LoggerFactory.getLogger(CommonConfigBundle.class);

  public static final String ENV_SYSTEM_PROPERTY = "io.bold.env";
  public static final String VERSION_SYSTEM_PROPERTY = "io.bold.version";
  public static final String TESTING_SYSTEM_PROPERTY = "io.bold.com.davidbyttow.sfe.testing";

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new ComponentConfigModule());

    String envPropertyValue = System.getProperty(ENV_SYSTEM_PROPERTY);
    if (Strings.isNullOrEmpty(envPropertyValue)) {
      logger.warn("-D{} not set, configuration will not be parameterized by environment", ENV_SYSTEM_PROPERTY);
      return;
    }

    Env env = new Env(envPropertyValue);

    String testingPropertyValue = System.getProperty(TESTING_SYSTEM_PROPERTY);
    if (Strings.nullToEmpty(testingPropertyValue).equals("true")) {
      env.setTesting(true);
    }

    String versionPropertyValue = System.getProperty(VERSION_SYSTEM_PROPERTY);
    if (Strings.isNullOrEmpty(versionPropertyValue)) {
      logger.warn("-D{} not set, configuration will not have version info", VERSION_SYSTEM_PROPERTY);
    } else {
      env.setVersion(versionPropertyValue);
    }

    ConfigurationSourceProvider configSourceProvider =
        new EnvSpecificConfigurationSourceProvider(env, bootstrap.dropwizard().getApplication().getName());
    bootstrap.dropwizard().setConfigurationSourceProvider(configSourceProvider);

    bootstrap.addModule(new AbstractModule() {
      @Override protected void configure() {
        bind(Env.class).toInstance(env);
      }
    });

    @SuppressWarnings("unchecked")
    Bootstrap<T> typedDropwizard = (Bootstrap<T>) bootstrap.dropwizard();
    typedDropwizard.setConfigurationFactoryFactory(new EnvSpecificConfigFactoryFactory<>(env));

    bootstrap.addCommand(new ConfiguredCommand<T>("dump-config", "Dumps the configuration for the service") {
      @Override
      protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration) throws Exception {
        YAMLFactory yaml = new YAMLFactory();
        yaml.createGenerator(System.out).writeObject(configuration);
      }
    });
  }

  @Override public void run(Injector injector, T configuration, Environment environment) {}
}
