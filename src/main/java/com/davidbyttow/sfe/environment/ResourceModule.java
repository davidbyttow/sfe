package com.davidbyttow.sfe.environment;

import com.google.inject.Provides;
import com.davidbyttow.sfe.common.ProviderOnlyModule;
import com.davidbyttow.sfe.service.Env;

public class ResourceModule extends ProviderOnlyModule {

  @Provides
  ResourceLoader provideResourceLoader(Env env) {
    // TODO(d): Add override?
    if (env.isLocalDevelopment()) {
      return new FileResourceLoader();
    }
    return new ClassResourceLoader();
  }
}
