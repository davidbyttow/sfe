package io.bold.sfe.web;

import com.google.inject.Injector;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.web.react.ReactTemplateModule;
import io.bold.sfe.web.react.ReactViewRenderer;
import io.bold.sfe.web.soy.SoyTemplateModule;
import io.bold.sfe.web.soy.SoyTemplateViewRenderer;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewMessageBodyWriter;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class TemplateBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {
  private static final Logger log = LoggerFactory.getLogger(TemplateBundle.class);

  private final List<String> reactFiles;
  private final List<String> soyFiles;

  public TemplateBundle(List<String> reactFiles, List<String> soyFiles) {
    this.reactFiles = reactFiles;
    this.soyFiles = soyFiles;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new SoyTemplateModule(soyFiles));
    bootstrap.addModule(new ReactTemplateModule(reactFiles));
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    SoyTemplateViewRenderer soyRenderer = injector.getInstance(SoyTemplateViewRenderer.class);
    ReactViewRenderer reactRenderer = injector.getInstance(ReactViewRenderer.class);
    PageViewRenderer pageRenderer = injector.getInstance(PageViewRenderer.class);
    environment.jersey().register(
      new ViewMessageBodyWriter(environment.metrics(),
      ImmutableList.of(soyRenderer, reactRenderer, pageRenderer)));
  }
}
