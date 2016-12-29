package io.bold.sfe.common;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.annotation.Nullable;

public final class Times {
  public static DateTime nowUtc() {
    return DateTime.now(DateTimeZone.UTC);
  }

  public static DateTime fromTimestampSeconds(long timestamp) {
    return fromTimestampMillis(timestamp * 1000);
  }

  public static DateTime fromTimestampMillis(long timestampMs) {
    return new DateTime(timestampMs);
  }

  public static long toTimestampMillis(DateTime dateTime) {
    return dateTime.getMillis();
  }

  public static long toTimestampSeconds(DateTime dateTime) {
    return dateTime.getMillis() / 1000;
  }

  public static DateTime nullToEpoch(@Nullable DateTime dateTime) {
    return (dateTime != null) ? dateTime : epoch();
  }

  public static DateTime epoch() {
    return new DateTime(0);
  }

  public static boolean isNullOrEpoch(@Nullable DateTime dateTime) {
    return dateTime == null || dateTime.getMillis() == 0;
  }

  private Times() {}
}
