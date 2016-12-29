package io.bold.sfe.service;

import com.google.common.base.Preconditions;

/** A runtime environment */
public class Env {
  private static final String DEV_ID = "development";
  private static final String STAGING_ID = "staging";
  private static final String PROD_ID = "prod";

  private final String id;
  private String version = "unspecified";
  private boolean isTesting = false;

  public Env(String id) {
    this.id = Preconditions.checkNotNull(id);
  }

  public String getId() {
    return id;
  }

  public boolean isStaging() {
    return id.equals(STAGING_ID);
  }

  public boolean isProduction() {
    return id.equals(PROD_ID);
  }

  public boolean isLocalDevelopment() {
    return id.equals(DEV_ID);
  }

  public boolean isTesting() {
    return isTesting;
  }

  @Override public String toString() {
    return id;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setTesting(boolean testing) {
    this.isTesting = testing;
  }

}
