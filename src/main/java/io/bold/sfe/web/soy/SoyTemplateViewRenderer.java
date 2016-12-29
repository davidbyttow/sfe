package io.bold.sfe.web.soy;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.template.soy.tofu.SoyTofu;
import io.bold.sfe.web.DataSerializer;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewRenderer;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Map;

/**
 * A {@link ViewRenderer} which renders Google Closure templates (soy).
 */
public class SoyTemplateViewRenderer implements ViewRenderer {

  private final Provider<SoyTofu> tofuProvider;
  private final DataSerializer serializer;

  @Inject SoyTemplateViewRenderer(Provider<SoyTofu> tofuProvider, DataSerializer serializer) {
    this.tofuProvider = tofuProvider;
    this.serializer = serializer;
  }

  @Override
  public boolean isRenderable(View view) {
    return view instanceof SoyFragment;
  }

  @Override
  public void configure(Map<String, String> options) {}

  @Override
  public void render(View view, Locale locale, OutputStream output) throws IOException, WebApplicationException {
    SoyTofu tofu = tofuProvider.get();
    SoyFragment soyFragment = (SoyFragment) view;
    String result = soyFragment.render(tofu, serializer);

    OutputStreamWriter writer = new OutputStreamWriter(output, Charsets.UTF_8);
    writer.write(result);
    writer.flush();
  }

  @Override
  public String getSuffix() {
    return ".soy";
  }
}
