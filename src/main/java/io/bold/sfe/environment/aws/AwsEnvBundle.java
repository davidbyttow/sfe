package io.bold.sfe.environment.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import io.bold.sfe.async.CompletedFuture;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.environment.aws.s3.S3ImageStore;
import io.bold.sfe.environment.aws.ses.SesMailSender;
import io.bold.sfe.environment.local.LocalInstanceMetadata;
import io.bold.sfe.image.ImageStore;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.inject.LazySingleton;
import io.bold.sfe.inject.LazySingletonScope;
import io.bold.sfe.mail.MailSender;
import io.bold.sfe.mail.Messages;
import io.bold.sfe.service.InstanceMetadata;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;

/** Bundle providing AWS-specific stuff */
public final class AwsEnvBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private static final Logger log = LoggerFactory.getLogger(AwsEnvBundle.class);

  private static final Region REGION = Region.getRegion(Regions.US_WEST_2);

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    log.info("Running on AWS");

    bootstrap.addModule(new AbstractModule() {
      @Override protected void configure() {
        bind(ImageStore.class).to(S3ImageStore.class).in(LazySingletonScope.get());
      }

      @Provides @LazySingleton MailSender provideMailSender(
        InstanceMetadata instanceMetadata,
        Provider<SesMailSender> sesMailSenderProvider) {
        if (instanceMetadata.isLocal()) {
          return new MailSender() {
            @Override public void send(Message message) {
              Futures.getUnchecked(sendAsync(message));
            }

            @Override public ListenableFuture<?> sendAsync(Message message) {
              log.info("Localhost ignoring send message:\n{}", Messages.getRawMessageAsString(message));
              return CompletedFuture.create();
            }
          };
        }
        return sesMailSenderProvider.get();
      }

      @Provides @LazySingleton InstanceMetadata instanceMetadata() {
        if (AwsInstanceMetadata.isAvailable()) {
          return new AwsInstanceMetadata();
        }
        return new LocalInstanceMetadata();
      }

      @Provides @LazySingleton AWSCredentialsProviderChain provideCredentialsChain() {
        return new DefaultAWSCredentialsProviderChain();
      }

      @Provides @LazySingleton AWSCredentials provideCredentials(AWSCredentialsProviderChain chain) {
        return chain.getCredentials();
      }

      @Provides @LazySingleton AmazonSimpleEmailServiceClient provideEmailClient(AWSCredentials credentials) {
        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);
        client.setRegion(REGION);
        return client;
      }

      @Provides @LazySingleton AmazonS3 provideS3Client(AWSCredentials credentials) {
        AmazonS3 client = new AmazonS3Client(credentials);
        client.setRegion(REGION);
        return client;
      }
    });
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    // TODO(matt): Install AWS Cloudwatch monitoring
  }
}
