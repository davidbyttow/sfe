package io.bold.sfe.common;

public final class Paths {

  public static String withTrailingSlash(String path) {
    return path.endsWith("/") ? path : path + "/";
  }

  public static String withoutLeadingSlash(String path) {
    return (path.startsWith("/")) ? path.substring(1) : path;
  }

  private Paths() {}
}
