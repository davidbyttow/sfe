package com.simplethingsllc.jersey.dagger;

import dagger.Component;
import io.bold.sfe.server.dagger.AppModule;
import io.bold.sfe.server.dagger.TestDaggerServer;

@Component(modules = AppModule.class)
public interface AppServer {
  TestDaggerServer server();
}
