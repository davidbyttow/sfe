package com.davidbyttow.sfe.environment.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.environment.aws.s3.S3ImageStore;
import com.davidbyttow.sfe.environment.aws.ses.SesMailSender;
import com.davidbyttow.sfe.image.ImageStore;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import com.davidbyttow.sfe.inject.LazySingleton;
import com.davidbyttow.sfe.inject.LazySingletonScope;
import com.davidbyttow.sfe.mail.MailSender;
import com.davidbyttow.sfe.service.InstanceMetadata;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/** Bundle providing AWS-specific stuff */
public final class AwsEnvBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private static final Logger log = LoggerFactory.getLogger(AwsEnvBundle.class);

  private static final Region REGION = Region.getRegion(Regions.US_WEST_2);

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    log.info("Running on AWS");

    bootstrap.addModule(new AbstractModule() {
      @Override protected void configure() {
        bind(MailSender.class).to(SesMailSender.class).in(LazySingletonScope.get());
        bind(ImageStore.class).to(S3ImageStore.class).in(LazySingletonScope.get());
      }

      @Provides @LazySingleton AWSCredentials provideCredentials(BasicServiceConfig config) {
        return config.aws;
      }

      @Provides @Singleton InstanceMetadata instanceMetadata() {
        return new AwsInstanceMetadata();
      }

      @Provides @LazySingleton AmazonSimpleEmailServiceClient provideEmailClient(AWSCredentials credentials) {
        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);
        client.setRegion(REGION);
        return client;
      }

      @Provides @LazySingleton AmazonS3Client provideS3Client(AWSCredentials credentials) {
        AmazonS3Client client = new AmazonS3Client(credentials);
        client.setRegion(REGION);
        return client;
      }
    });
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    // TODO(matt): Install AWS Cloudwatch monitoring
  }
}
