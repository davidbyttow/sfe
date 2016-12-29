package io.bold.sfe.integrations.stripe;

import com.google.inject.Injector;
import com.stripe.Stripe;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StripeBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private static final Logger log = LoggerFactory.getLogger(StripeBundle.class);

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    StripeConfig stripe = configuration.integrations.stripe;
    if (stripe != null) {
      log.info("Initialized Stripe api key");
      Stripe.apiKey = stripe.secretKey;
    } else {
      log.info("Stripe api key not configured");
    }
  }
}
