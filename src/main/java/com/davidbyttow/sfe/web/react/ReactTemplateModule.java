package com.davidbyttow.sfe.web.react;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import com.davidbyttow.sfe.common.ProviderOnlyModule;
import com.davidbyttow.sfe.environment.ResourceLoader;
import com.davidbyttow.sfe.service.Env;

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
