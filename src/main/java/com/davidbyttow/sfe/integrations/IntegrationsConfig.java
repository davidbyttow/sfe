package com.davidbyttow.sfe.integrations;

import com.davidbyttow.sfe.integrations.algolia.AlgoliaConfig;
import com.davidbyttow.sfe.integrations.cm.CampaignMonitorConfig;
import com.davidbyttow.sfe.integrations.embedly.EmbedlyConfig;
import com.davidbyttow.sfe.integrations.fullstory.FullstoryConfig;
import com.davidbyttow.sfe.integrations.google.GoogleAnalyticsConfig;
import com.davidbyttow.sfe.integrations.mixpanel.MixpanelConfig;
import com.davidbyttow.sfe.integrations.pusher.PusherConfig;
import com.davidbyttow.sfe.integrations.sentry.SentryConfig;
import com.davidbyttow.sfe.integrations.slack.SlackConfig;
import com.davidbyttow.sfe.integrations.stripe.StripeConfig;
import com.davidbyttow.sfe.integrations.twitter.TwitterAuthConfig;

public class IntegrationsConfig {
  public AlgoliaConfig algolia;
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
}
