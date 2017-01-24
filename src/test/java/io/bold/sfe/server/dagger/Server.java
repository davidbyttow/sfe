package io.bold.sfe.server.dagger;

import dagger.Component;
import io.bold.sfe.server.TestServer;

@Component(modules = ApplicationModule.class)
public interface Server {
  TestServer server();
}
