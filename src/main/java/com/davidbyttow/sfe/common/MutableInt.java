package com.davidbyttow.sfe.common;

public class MutableInt {

  private int value;

  private MutableInt() {
    this.value = 0;
  }

  private MutableInt(int value) {
    this.value = value;
  }

  public void set(int value) {
    this.value = value;
  }

  public int increment(int delta) {
    this.value += delta;
    return this.value;
  }

  public int get() {
    return value;
  }

  public static MutableInt of(int value) {
    return new MutableInt(value);
  }
}
