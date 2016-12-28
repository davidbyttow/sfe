package com.davidbyttow.sfe.integrations.slack;

import java.util.HashMap;
import java.util.Map;

public class SlackConfig {
  public String clientId = "";
  public String clientSecret = "";
  public String verificationToken = "";
  public String signInRedirectUri;
  public String addToSlackRedirectUri;
  public Map<String, String> internalWebhooks = new HashMap<>();
}
