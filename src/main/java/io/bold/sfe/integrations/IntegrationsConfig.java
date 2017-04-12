package io.bold.sfe.integrations;

import com.simplethingsllc.framework.integrations.apns.ApnsConfig;
import com.simplethingsllc.framework.integrations.twilio.TwilioConfig;
import io.bold.sfe.integrations.algolia.AlgoliaConfig;
import io.bold.sfe.integrations.cm.CampaignMonitorConfig;
import io.bold.sfe.integrations.embedly.EmbedlyConfig;
import io.bold.sfe.integrations.fullstory.FullstoryConfig;
import io.bold.sfe.integrations.google.GoogleAnalyticsConfig;
import io.bold.sfe.integrations.mixpanel.MixpanelConfig;
import io.bold.sfe.integrations.pusher.PusherConfig;
import io.bold.sfe.integrations.sentry.SentryConfig;
import io.bold.sfe.integrations.slack.SlackConfig;
import io.bold.sfe.integrations.stripe.StripeConfig;
import io.bold.sfe.integrations.twitter.TwitterAuthConfig;

public class IntegrationsConfig {
  public AlgoliaConfig algolia;
  public ApnsConfig apns;
  public CampaignMonitorConfig campaignMonitor;
  public EmbedlyConfig embedly;
  public GoogleAnalyticsConfig googleAnalytics;
  public MixpanelConfig mixpanel;
  public PusherConfig pusher;
  public SentryConfig sentry;
  public SlackConfig slack;
  public FullstoryConfig fullstory;
  public StripeConfig stripe;
  public TwitterAuthConfig twitter;
  public TwilioConfig twilio;
}
