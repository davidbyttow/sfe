package io.bold.sfe.common;

public class Mutable<T> {

  private T value;

  private Mutable(T value) {
    this.value = value;
  }

  public void set(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  public static <T> Mutable<T> of(T value) {
    return new Mutable<>(value);
  }
}
