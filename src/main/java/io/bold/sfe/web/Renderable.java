package io.bold.sfe.web;

import com.google.common.base.Charsets;
import io.dropwizard.views.View;

import javax.annotation.Nullable;

public abstract class Renderable extends View {

  private final Object data;

  protected Renderable(String templateName, @Nullable Object data) {
    super(templateName, Charsets.UTF_8);
    this.data = data;
  }

  @Nullable public Object getData() {
    return data;
  }
}
