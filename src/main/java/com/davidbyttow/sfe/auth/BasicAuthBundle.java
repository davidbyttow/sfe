package com.davidbyttow.sfe.auth;

import com.google.inject.Injector;
import com.davidbyttow.sfe.config.BasicServiceConfig;
import com.davidbyttow.sfe.inject.ConfiguredGuiceBundle;
import com.davidbyttow.sfe.inject.GuiceBootstrap;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.util.Map;

public final class BasicAuthBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {

  private final String realm;

  public BasicAuthBundle(String realm) {
    this.realm = realm;
  }

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {}

  @Override public void run(Injector injector, T config, Environment environment) throws Exception {
    Map<String, String> admins = config.admins;
    environment.jersey().register(new AuthDynamicFeature(
      new BasicCredentialAuthFilter.Builder<UserPrincipal>()
        .setAuthenticator(new BasicAuthenticator(admins))
        .setAuthorizer(new BasicAuthorizer())
        .setRealm(realm)
        .buildAuthFilter()));
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(UserPrincipal.class));
  }
}
