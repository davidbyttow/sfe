package com.davidbyttow.sfe.auth;

import io.dropwizard.auth.Authorizer;

public class BasicAuthorizer implements Authorizer<UserPrincipal> {
  @Override
  public boolean authorize(UserPrincipal user, String role) {
    return (user != null) && user.hasRole(role);
  }
}
