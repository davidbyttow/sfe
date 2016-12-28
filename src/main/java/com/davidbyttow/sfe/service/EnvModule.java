package com.davidbyttow.sfe.service;

import com.google.inject.Provides;
import com.davidbyttow.sfe.common.ProviderOnlyModule;
import com.davidbyttow.sfe.inject.LazySingleton;

public final class EnvModule extends ProviderOnlyModule {
  @Provides @LazySingleton InstanceHostProvider platform(InstanceMetadata metadata) {
    return metadata.getPlatform();
  }
}
