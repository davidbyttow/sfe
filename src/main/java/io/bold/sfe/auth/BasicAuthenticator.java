package io.bold.sfe.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Map;
import java.util.Optional;

public class BasicAuthenticator implements Authenticator<BasicCredentials, UserPrincipal> {

  private final Map<String, String> admins;

  public BasicAuthenticator(Map<String, String> admins) {
    this.admins = admins;
  }

  @Override
  public Optional<UserPrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
    String password = admins.get(credentials.getUsername());
    if (password != null && password.equals(credentials.getPassword())) {
      return Optional.of(new UserPrincipal(credentials.getUsername(), "ADMIN"));
    }
    return Optional.empty();
  }
}
