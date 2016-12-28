package com.davidbyttow.sfe.common;

public class Pair<T, U> {
  public final T _1;
  public final U _2;

  public Pair(T _1, U _2) {
    this._1 = _1;
    this._2 = _2;
  }

  public static <T, U> Pair<T, U> of(T _1, U _2) {
    return new Pair<>(_1, _2);
  }

  public T first() {
    return _1;
  }

  public U second() {
    return _2;
  }

  @Override public boolean equals(Object other) {
    if (other == this) return false;
    if (!(other instanceof Pair)) return false;
    Pair p = (Pair) other;
    return (_1 == null ? p._1 == null : _1.equals(p._1)) && (_2 == null ? p._2 == null : _2.equals(p._2));
  }

  @Override public String toString() {
    return String.format("<%s, %s>", _1, _2);
  }
}

