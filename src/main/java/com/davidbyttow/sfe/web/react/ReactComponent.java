package com.davidbyttow.sfe.web.react;

import com.google.common.base.Throwables;
import com.davidbyttow.sfe.web.DataSerializer;
import com.davidbyttow.sfe.web.Renderable;

import javax.annotation.Nullable;
import java.io.IOException;

public class ReactComponent extends Renderable {

  private final Object data;
  private final String componentClassName;

  public ReactComponent(String templateName, Object data) {
    super(templateName, data);
    this.data = data;
    this.componentClassName = templateName;
  }

  public String getComponentClassName() {
    return componentClassName;
  }

  @Nullable @Override public Object getData() {
    return data;
  }
  String render(ReactBridge react, DataSerializer serializer) {
    try {
      return react.render(componentClassName, serializer.serializeToMap(data), null);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
