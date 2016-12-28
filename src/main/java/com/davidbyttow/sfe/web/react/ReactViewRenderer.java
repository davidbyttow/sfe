package com.davidbyttow.sfe.web.react;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.davidbyttow.sfe.web.DataSerializer;
import io.dropwizard.views.View;
import io.dropwizard.views.ViewRenderer;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Map;

/**
 * A {@link ViewRenderer} which renders React components.
 */
public class ReactViewRenderer implements ViewRenderer {

  private final ReactBridge react;
  private final DataSerializer serializer;

  @Inject ReactViewRenderer(ReactBridge react, DataSerializer serializer) {
    this.react = react;
    this.serializer = serializer;
  }

  @Override
  public boolean isRenderable(View view) {
    return view instanceof ReactComponent;
  }

  @Override
  public void configure(Map<String, String> options) {
  }

  @Override
  public void render(View view, Locale locale, OutputStream output) throws IOException, WebApplicationException {
    ReactComponent component = (ReactComponent) view;
    String result = component.render(react, serializer);

    OutputStreamWriter writer = new OutputStreamWriter(output, Charsets.UTF_8);
    writer.write(result);
    writer.flush();
  }

  @Override
  public String getSuffix() {
    return ".jsx";
  }
}
