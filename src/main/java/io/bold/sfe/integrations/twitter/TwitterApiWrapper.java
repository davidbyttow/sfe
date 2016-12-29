package io.bold.sfe.integrations.twitter;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import io.bold.sfe.config.BasicServiceConfig;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterApiWrapper {

  private static final String OAUTH2_TOKEN_TYPE = "bearer";

  private final BasicServiceConfig config;

  @Inject TwitterApiWrapper(BasicServiceConfig config) {
    this.config = config;
  }

  public Twitter full() {
    TwitterAuthConfig twitter = validateCredentials(config);

    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.setOAuthConsumerKey(twitter.apiKey);
    builder.setOAuthConsumerSecret(twitter.apiSecret);

    TwitterFactory factory = new TwitterFactory(builder.build());
    return factory.getInstance();
  }

  public Twitter appOnly() {
    TwitterAuthConfig twitter = validateCredentials(config);
    Preconditions.checkState(Strings.emptyToNull(twitter.appBearerToken) != null);

    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.setOAuthConsumerKey(twitter.apiKey);
    builder.setOAuthConsumerSecret(twitter.apiSecret);
    builder.setOAuth2AccessToken(twitter.appBearerToken);
    builder.setOAuth2TokenType(OAUTH2_TOKEN_TYPE);
    builder.setApplicationOnlyAuthEnabled(true);

    TwitterFactory factory = new TwitterFactory(builder.build());
    return factory.getInstance();
  }

  private TwitterAuthConfig validateCredentials(BasicServiceConfig config) {
    TwitterAuthConfig twitter = Preconditions.checkNotNull(config.integrations.twitter);
    Preconditions.checkState(Strings.emptyToNull(twitter.apiKey) != null);
    Preconditions.checkState(Strings.emptyToNull(twitter.apiSecret) != null);
    return twitter;
  }
}
