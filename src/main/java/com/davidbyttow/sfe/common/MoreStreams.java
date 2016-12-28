package com.davidbyttow.sfe.common;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/** More stream helpers */
public final class MoreStreams {
  /** Iterate over two streams processing each element in both in order */
  public static <T, U, R> Stream<R> zip(Stream<T> s1, Stream<U> s2, BiFunction<T, U, R> f) {
    Iterator<U> itr = s2.iterator();
    return s1.filter(x -> itr.hasNext()).map(x -> f.apply(x, itr.next()));
  }

  /** Create a stream out of two substreams */
  public static <T, U> Stream<Pair<T, U>> zip(Stream<T> s1, Stream<U> s2) {
    return zip(s1, s2, Pair::new);
  }

  /** Create a stream from another stream paired with its index */
  public static <T> Stream<Pair<T, Long>> zipWithIndex(Stream<T> s1) {
    return zip(s1, LongStream.iterate(0, n -> n + 1).boxed());
  }

  private MoreStreams() {}
}

