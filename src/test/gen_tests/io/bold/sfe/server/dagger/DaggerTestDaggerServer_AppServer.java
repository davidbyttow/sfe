package io.bold.sfe.server.dagger;

import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerTestDaggerServer_AppServer implements TestDaggerServer.AppServer {
  private Provider<TestDaggerServer> provideServerProvider;

  private DaggerTestDaggerServer_AppServer(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static TestDaggerServer.AppServer create() {
    return builder().build();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.provideServerProvider =
        TestDaggerServer_AppModule_ProvideServerFactory.create(builder.appModule);
  }

  @Override
  public TestDaggerServer server() {
    return provideServerProvider.get();
  }

  public static final class Builder {
    private TestDaggerServer.AppModule appModule;

    private Builder() {}

    public TestDaggerServer.AppServer build() {
      if (appModule == null) {
        this.appModule = new TestDaggerServer.AppModule();
      }
      return new DaggerTestDaggerServer_AppServer(this);
    }

    public Builder appModule(TestDaggerServer.AppModule appModule) {
      this.appModule = Preconditions.checkNotNull(appModule);
      return this;
    }
  }
}
