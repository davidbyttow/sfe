package io.bold.sfe.server.dagger;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
  @Provides static TestDaggerServer provideServer() {
    return new TestDaggerServer();
  }
}
