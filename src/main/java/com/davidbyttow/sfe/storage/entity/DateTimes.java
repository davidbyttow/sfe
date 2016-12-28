package com.davidbyttow.sfe.storage.entity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

final class DateTimes {

  private static final DateTimeFormatter DATETIME_SERIALIZATION_FORMATTER =
      DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSS").withZoneUTC();

  private static final DateTimeFormatter DATETIME_DESERIALIZATION_FORMATTER =
      DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC();

  static String toSqlValue(DateTime dateTime) {
    return dateTime.toString(DATETIME_SERIALIZATION_FORMATTER);
  }

  static DateTime fromSqlValue(String value) {
    return DateTime.parse(value, DATETIME_DESERIALIZATION_FORMATTER);
  }

  private DateTimes() {}
}
