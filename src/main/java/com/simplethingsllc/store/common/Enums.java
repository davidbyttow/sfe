package com.simplethingsllc.store.common;

public final class Enums {
  public static String storedName(Enum<?> e) {
    return e.name().toUpperCase();
  }

  private Enums() {}
}
