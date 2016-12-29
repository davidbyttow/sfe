package io.bold.sfe.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MoreRandoms {
  public static long nextLongInclusive(Random r, long min, long max) {
    long inclusive = max - min + 1;
    long n = (min >= 0) ? Math.abs(r.nextLong()) : r.nextLong();
    return (n % inclusive) + min;
  }

  public static long nextLongInclusive(long min, long max) {
    return nextLongInclusive(ThreadLocalRandom.current(), min, max);
  }

  public static int nextIntInclusive(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public static long nextLong() {
    return ThreadLocalRandom.current().nextLong();
  }

  public static int nextInt(int n) {
    return ThreadLocalRandom.current().nextInt(n);
  }

  public static <T> T nextIn(T[] values) {
    return values[Math.abs(nextInt(values.length))];
  }

  public static <T> T nextIn(List<T> values) {
    return values.get(nextInt(values.size()));
  }

  public static long nextIn(long[] values) {
    return values[Math.abs(nextInt(values.length))];
  }

  public static int nextIn(int[] values) {
    return values[Math.abs(nextInt(values.length))];
  }

  public static double nextFloat() {
    return ThreadLocalRandom.current().nextFloat();
  }

  public static String randomUuid() {
    return UUID.randomUUID().toString();
  }

  public static String randomMd5() {
    return Hashes.md5(randomUuid());
  }

  public static String randomAlphabetic(int count) {
    return RandomStringUtils.randomAlphabetic(count).toLowerCase();
  }

  public static String randomAlphanumeric(int count) {
    return RandomStringUtils.randomAlphanumeric(count).toLowerCase();
  }

  public static String randomAlphanumeric(int count, Random random) {
    return RandomStringUtils.random(count, 0, 0, true, true, null, random);
  }

  private MoreRandoms() {}
}
