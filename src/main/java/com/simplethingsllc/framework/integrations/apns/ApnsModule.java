package com.simplethingsllc.framework.integrations.apns;

import com.google.inject.Provides;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.DeliveryError;
import io.bold.sfe.common.ProviderOnlyModule;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.environment.ClassLoaders;
import io.bold.sfe.inject.LazySingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.io.InputStream;

public final class ApnsModule extends ProviderOnlyModule {
  private static final Logger log = LoggerFactory.getLogger(ApnsModule.class);

  @Provides
  @LazySingleton
  ApnsService provideApnsService(BasicServiceConfig config) {
    String keyFile = config.integrations.apns.prodKeyFile;
    InputStream is = ClassLoaders.open(keyFile);
    if (is == null) {
      throw new BadRequestException();
    }

    ApnsServiceBuilder builder = APNS.newService()
      .withCert(is, config.integrations.apns.keyPassPhrase);
    builder.withDelegate(new ApnsDelegate() {
      @Override public void messageSent(ApnsNotification message, boolean resent) {
        log.warn("Message sent");
      }

      @Override public void messageSendFailed(ApnsNotification message, Throwable e) {
        log.warn("Message send failed", e);
      }

      @Override public void connectionClosed(DeliveryError e, int messageIdentifier) {
        log.warn("connection closed", e);
      }

      @Override public void cacheLengthExceeded(int newCacheLength) {
        log.warn("cache length exceeded");
      }

      @Override public void notificationsResent(int resendCount) {
        log.warn("resent");
      }
    });
    builder.withAppleDestination(true);
    return builder.build();
  }
}
