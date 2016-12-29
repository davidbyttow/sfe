package io.bold.sfe.web.react;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import io.bold.sfe.common.ProviderOnlyModule;
import io.bold.sfe.environment.ResourceLoader;
import io.bold.sfe.service.Env;

import java.util.List;

public class ReactTemplateModule extends ProviderOnlyModule {

  private final List<String> files;

  public ReactTemplateModule(List<String> files) {
    this.files = files;
  }

  @Provides ReactBridge provideReactBridge(ResourceLoader resourceLoader, Env env, ObjectMapper objectMapper) {
    return ReactBridge.newReactBridge(resourceLoader, env, objectMapper, files);
  }
}
