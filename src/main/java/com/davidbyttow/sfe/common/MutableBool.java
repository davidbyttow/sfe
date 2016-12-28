package com.davidbyttow.sfe.common;

public class MutableBool {

  private boolean value;

  private MutableBool(boolean value) {
    this.value = value;
  }

  public void set(boolean value) {
    this.value = value;
  }

  public boolean get() {
    return this.value;
  }

  public static MutableBool of(boolean value) {
    return new MutableBool(value);
  }
}
