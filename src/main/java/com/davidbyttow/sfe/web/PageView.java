package com.davidbyttow.sfe.web;

import java.util.ArrayList;
import java.util.List;

public class PageView extends Renderable {

  public static class Metadata {
    public String title;
    public String description;
    public String imageUrl;
    public String url;
  }

  private final String soyTemplateName;
  private final String title;
  private final List<String> cssUrls;
  private final List<String> jsUrls;
  private final String reactComponentName;
  private final Object globalData;
  private final boolean disableServerSideRendering;
  private final Metadata metadata;

  private PageView(Builder builder) {
    super(builder.soyTemplateName, builder.data);
    this.soyTemplateName = builder.soyTemplateName;
    this.title = builder.title;
    this.jsUrls = builder.jsUrls;
    this.cssUrls = builder.cssUrls;
    this.reactComponentName = builder.componentName;
    this.globalData = builder.globalData;
    this.disableServerSideRendering = builder.disableServerSideRendering;
    this.metadata = (builder.metadata != null) ? builder.metadata : new Metadata();
  }

  public static Builder newBuilder(String templateName) {
    return new Builder(templateName);
  }

  public String getTitle() {
    return title;
  }

  public List<String> getCssUrls() {
    return cssUrls;
  }

  public List<String> getJsUrls() {
    return jsUrls;
  }

  public String getSoyTemplateName() {
    return soyTemplateName;
  }

  public String getReactComponentName() {
    return reactComponentName;
  }

  public Object getGlobalData() {
    return globalData;
  }

  public boolean isServerSideRenderingDisabled() {
    return disableServerSideRendering;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public static class Builder {
    private final String soyTemplateName;
    private String title = "";
    private List<String> cssUrls = new ArrayList<>();
    private List<String> jsUrls = new ArrayList<>();
    private String componentName = "";
    private Object data = null;
    private Object globalData;
    private boolean disableServerSideRendering;
    private Metadata metadata;

    Builder(String soyTemplateName) {
      this.soyTemplateName = soyTemplateName;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder addJsUrl(String jsUrl) {
      jsUrls.add(jsUrl);
      return this;
    }

    public Builder addCssUrl(String cssUrl) {
      cssUrls.add(cssUrl);
      return this;
    }

    public Builder setComponentName(String componentName) {
      this.componentName = componentName;
      return this;
    }

    public Builder setData(Object data) {
      this.data = data;
      return this;
    }

    public Builder setGlobalData(Object globalData) {
      this.globalData = globalData;
      return this;
    }

    public Builder setDisableServerSideRendering(boolean disabled) {
      this.disableServerSideRendering = disabled;
      return this;
    }

    public Builder setMetadata(Metadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public PageView build() {
      return new PageView(this);
    }
  }
}
