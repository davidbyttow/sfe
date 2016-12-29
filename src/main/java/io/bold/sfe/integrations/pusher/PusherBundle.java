package io.bold.sfe.integrations.pusher;

import com.google.inject.Injector;
import com.google.inject.Provides;
import io.bold.sfe.common.ProviderOnlyModule;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.inject.LazySingleton;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class PusherBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private static final Logger log = LoggerFactory.getLogger(PusherBundle.class);

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new ProviderOnlyModule() {

      @Provides
      @Nullable
      PusherConfig providerPusherConfig(BasicServiceConfig config) {
        return config.integrations.pusher;
      }

      @Provides
      @LazySingleton
      PusherNotifier providePusherNotifier(@Nullable PusherConfig pusherConfig, HttpClient httpClient)
          throws InvalidKeyException, NoSuchAlgorithmException {
        if (pusherConfig == null) {
          return new PusherNotifier((json) -> {
            log.info("Pusher not configured for payload: {}", json);
          });
        }
        return new PusherNotifier(new SecureHttpPusherConnection(
            httpClient, pusherConfig.getAppId(), pusherConfig.getAppKey(), pusherConfig.getAppSecret()));
      }
    });
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
  }
}
