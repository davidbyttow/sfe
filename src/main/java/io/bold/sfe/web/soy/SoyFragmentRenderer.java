package io.bold.sfe.web.soy;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.template.soy.tofu.SoyTofu;
import io.bold.sfe.web.DataSerializer;

import javax.annotation.Nullable;
import java.io.IOException;

public class SoyFragmentRenderer {
  private final Provider<SoyTofu> soyTofuProvider;
  private final DataSerializer dataSerializer;

  @Inject SoyFragmentRenderer(Provider<SoyTofu> soyTofuProvider, DataSerializer dataSerializer) {
    this.soyTofuProvider = soyTofuProvider;
    this.dataSerializer = dataSerializer;
  }

  public String renderFragment(String templateName, Object data) {
    return renderFragment(templateName, data, null);
  }

  public String renderFragment(String templateName, Object data, @Nullable Object ijData) {
    SoyTofu tofu = soyTofuProvider.get();
    SoyTofu.Renderer renderer = tofu.newRenderer(templateName);
    try {
      renderer.setData(dataSerializer.serializeToMap(data));
      if (ijData != null) {
        renderer.setIjData(dataSerializer.serializeToMap(ijData));
      }
      return renderer.render();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
