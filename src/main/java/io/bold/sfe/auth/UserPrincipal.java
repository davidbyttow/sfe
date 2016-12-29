package io.bold.sfe.auth;

import java.security.Principal;
import java.util.Objects;

public class UserPrincipal implements Principal {

  private final String name;
  private final String role;

  UserPrincipal(String name, String role) {
    this.name = name;
    this.role = role;
  }

  public boolean hasRole(String roleToCheck) {
    return role.equals("ADMIN") || role.equals(roleToCheck);
  }

  @Override public boolean equals(Object another) {
    if (another == this) return true;
    if (another == null || !(another instanceof UserPrincipal)) return false;
    UserPrincipal other = (UserPrincipal) another;
    return Objects.equals(name, other.name) && Objects.equals(role, other.role);
  }

  @Override public String toString() {
    return name;
  }

  @Override public int hashCode() {
    return Objects.hash(name, role);
  }

  @Override public String getName() {
    return name;
  }
}
