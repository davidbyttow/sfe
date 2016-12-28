package com.davidbyttow.sfe.web.soy;

import com.google.template.soy.tofu.SoyTofu;
import com.davidbyttow.sfe.web.DataSerializer;
import com.davidbyttow.sfe.web.Renderable;

import java.io.IOException;
import java.util.Map;

public class SoyFragment extends Renderable {
  public SoyFragment(String templateName, Object data) {
    super(templateName, data);
  }

  private String getQualifiedTemplateName() {
    String templateName = getTemplateName();
    int index = templateName.lastIndexOf('/');
    if (index >= 0) {
      templateName = templateName.substring(index + 1);
    }
    return templateName;
  }

  String render(SoyTofu tofu, DataSerializer serializer) throws IOException {
    SoyTofu.Renderer renderer = tofu.newRenderer(getQualifiedTemplateName());
    Map<String, Object> soyData = serializer.serializeToMap(getData());
    renderer.setData(soyData);
    return renderer.render();
  }
}
