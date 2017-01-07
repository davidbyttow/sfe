package io.bold.sfe.web;

import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.integrations.IntegrationsConfig;
import io.bold.sfe.service.Env;
import io.bold.sfe.web.js.InjectedScripts;
import io.bold.sfe.web.react.ReactBridge;
import io.bold.sfe.web.webpack.WebpackAssetResolver;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.html.types.SafeScripts;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SanitizedContents;
import com.google.template.soy.tofu.SoyTofu;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewRenderer;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PageViewRenderer implements ViewRenderer {

  private final Provider<SoyTofu> tofuProvider;
  private final ReactBridge reactBridge;
  private final DataSerializer serializer;
  private final WebpackAssetResolver webpackAssetResolver;
  private final BasicServiceConfig config;
  private final Env env;

  @Inject PageViewRenderer(Provider<SoyTofu> tofuProvider,
                           ReactBridge reactBridge,
                           DataSerializer serializer,
                           WebpackAssetResolver webpackAssetResolver,
                           BasicServiceConfig config,
                           Env env) {
    this.tofuProvider = tofuProvider;
    this.reactBridge = reactBridge;
    this.serializer = serializer;
    this.webpackAssetResolver = webpackAssetResolver;
    this.config = config;
    this.env = env;
  }

  @Override
  public boolean isRenderable(View view) {
    return view instanceof PageView;
  }

  @Override
  public void configure(Map<String, String> options) {}

  @Override
  public void render(View view, Locale locale, OutputStream output) throws IOException, WebApplicationException {
    PageView pageView = (PageView) view;

    String bodyHtml = "";
    String reactComponentName = pageView.getReactComponentName();

    boolean forceClientSideRendering = pageView.isServerSideRenderingDisabled();

    Map<String, Object> props = new HashMap<>(serializer.serializeToMap(pageView.getData()));
    if (!Strings.isNullOrEmpty(reactComponentName) && !forceClientSideRendering) {
      bodyHtml = reactBridge.render(reactComponentName, props, pageView.getGlobalData());
    }

    SoyTofu tofu = tofuProvider.get();
    SoyTofu.Renderer outer = tofu.newRenderer(pageView.getSoyTemplateName());

    // Then inject it into the outer template.
    Map<String, Object> outerData = new HashMap<>();

    String assetBaseUrl = config.assetBase;

    List<String> jsUrls = pageView.getJsUrls().stream()
      .map(webpackAssetResolver::getAssetFile)
      .map(url -> url = assetBaseUrl + url)
      .collect(Collectors.toList());
    List<String> cssUrls = pageView.getCssUrls().stream()
      .map(webpackAssetResolver::getAssetFile)
      .map(url -> url = assetBaseUrl + url)
      .collect(Collectors.toList());

    String injected = getInjectedScripts();
    SanitizedContent headJs = null;
    if (!Strings.isNullOrEmpty(injected)) {
      headJs = SanitizedContents.fromSafeScript(SafeScripts.fromConstant(injected));
    }

    // TODO(d): Use a POJO for this.
    outerData.put("title", pageView.getTitle());
    outerData.put("bodyHtml", bodyHtml);
    outerData.put("jsUrls", jsUrls);
    outerData.put("baseUrl", assetBaseUrl);
    outerData.put("cssUrls", cssUrls);
    if (headJs != null) {
      outerData.put("headJs", headJs);
    }
    outerData.put("propsJson", serializer.serializeToString(props));
    outerData.put("globJson", serializer.serializeToString(pageView.getGlobalData()));
    outerData.put("glob", serializer.serializeToMap(pageView.getGlobalData()));
    outerData.put("pageMetadata", serializer.serializeToMap(pageView.getMetadata()));
    if (!Strings.isNullOrEmpty(reactComponentName)) {
      outerData.put("componentClassName", reactComponentName);
    }

    outer.setData(outerData);

    OutputStreamWriter writer = new OutputStreamWriter(output, Charsets.UTF_8);
    outer.render(writer);
    writer.flush();
  }

  protected String getInjectedScripts() {
    IntegrationsConfig integrations = config.integrations;
    StringBuilder builder = new StringBuilder();

    if (integrations.googleAnalytics != null) {
      builder.append(InjectedScripts.forGoogleAnalytics(integrations.googleAnalytics));
    }

    if (integrations.mixpanel != null) {
      builder.append(InjectedScripts.forMixpanel(integrations.mixpanel));
    }

    if (integrations.fullstory != null) {
      builder.append(InjectedScripts.forFullstory(integrations.fullstory));
    }
    return builder.toString();
  }

  @Override public String getSuffix() {
    return ".soy";
  }
}
