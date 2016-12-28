package com.davidbyttow.sfe.environment.local;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.davidbyttow.sfe.async.CompletedFuture;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.environment.aws.s3.S3ImageStore;
import com.davidbyttow.sfe.image.ImageStore;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import com.davidbyttow.sfe.inject.LazySingleton;
import com.davidbyttow.sfe.inject.LazySingletonScope;
import com.davidbyttow.sfe.mail.MailSender;
import com.davidbyttow.sfe.mail.Messages;
import com.davidbyttow.sfe.service.InstanceMetadata;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;

/** Provides environment specific bindings and services for running locally */
public class LocalEnvBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {
  private static final Logger log = LoggerFactory.getLogger(LocalEnvBundle.class);

  private static final Region REGION = Region.getRegion(Regions.US_WEST_2);

  @Override final public void initialize(GuiceBootstrap<?> bootstrap) {
    log.info("Running in local development environment");
    bootstrap.addModule(new AbstractModule() {

      @Override protected void configure() {
        bind(MailSender.class).toInstance(new MailSender() {
          @Override public void send(Message message) {
            Futures.getUnchecked(sendAsync(message));
          }

          @Override public ListenableFuture<?> sendAsync(Message message) {
            log.info("Localhost ignoring send mail of message:\n{}", Messages.getRawMessageAsString(message));
            return CompletedFuture.create();
          }
        });
      }

      @Provides InstanceMetadata instanceMetadata() {
        return new LocalInstanceMetadata();
      }
    });

    // For images, we will use S3 sometimes...
    bootstrap.addModule(new AbstractModule() {
      @Override protected void configure() {
        bind(ImageStore.class).to(S3ImageStore.class).in(LazySingletonScope.get());
      }

      @Provides @LazySingleton AWSCredentials provideCredentials(BasicServiceConfig config) {
        return config.aws;
      }

      @Provides @LazySingleton AmazonS3Client provideS3Client(AWSCredentials credentials) {
        AmazonS3Client client = new AmazonS3Client(credentials);
        client.setRegion(REGION);
        return client;
      }
    });

  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
  }
}
