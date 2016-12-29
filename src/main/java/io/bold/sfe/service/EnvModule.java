package io.bold.sfe.service;

import com.google.inject.Provides;
import io.bold.sfe.common.ProviderOnlyModule;
import io.bold.sfe.inject.LazySingleton;

public final class EnvModule extends ProviderOnlyModule {
  @Provides @LazySingleton InstanceHostProvider platform(InstanceMetadata metadata) {
    return metadata.getPlatform();
  }
}
