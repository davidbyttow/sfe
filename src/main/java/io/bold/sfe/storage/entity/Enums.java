package io.bold.sfe.storage.entity;

public final class Enums {
  public static String storedName(Enum<?> e) {
    return e.name().toUpperCase();
  }

  private Enums() {}
}
