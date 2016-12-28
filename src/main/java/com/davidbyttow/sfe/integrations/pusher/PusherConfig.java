package com.davidbyttow.sfe.integrations.pusher;

import org.hibernate.validator.constraints.NotEmpty;

public class PusherConfig {
  @NotEmpty private int appId;
  @NotEmpty private String appKey;
  @NotEmpty private String appSecret;
  private boolean encrypted = true;

  public int getAppId() {
    return appId;
  }

  public void setAppId(int appId) {
    this.appId = appId;
  }

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public boolean isEncrypted() {
    return encrypted;
  }
}
