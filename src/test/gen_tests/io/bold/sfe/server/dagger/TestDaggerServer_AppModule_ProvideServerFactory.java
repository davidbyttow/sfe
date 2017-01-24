package io.bold.sfe.server.dagger;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class TestDaggerServer_AppModule_ProvideServerFactory
    implements Factory<TestDaggerServer> {
  private final TestDaggerServer.AppModule module;

  public TestDaggerServer_AppModule_ProvideServerFactory(TestDaggerServer.AppModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public TestDaggerServer get() {
    return Preconditions.checkNotNull(
        module.provideServer(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<TestDaggerServer> create(TestDaggerServer.AppModule module) {
    return new TestDaggerServer_AppModule_ProvideServerFactory(module);
  }

  /** Proxies {@link TestDaggerServer.AppModule#provideServer()}. */
  public static TestDaggerServer proxyProvideServer(TestDaggerServer.AppModule instance) {
    return instance.provideServer();
  }
}
