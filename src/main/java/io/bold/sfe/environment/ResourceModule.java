package io.bold.sfe.environment;

import com.google.inject.Provides;
import io.bold.sfe.common.ProviderOnlyModule;
import io.bold.sfe.service.Env;

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
