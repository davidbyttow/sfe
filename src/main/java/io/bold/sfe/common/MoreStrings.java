package io.bold.sfe.common;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * String helpers.
 */
public class MoreStrings {
  private static final Pattern COMBINING_DIACRITICALS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

  public static String fromUtf8(byte[] contents) {
    return new String(contents, Charsets.UTF_8);
  }

  public static byte[] toUtf8(String s) {
    return s.getBytes(Charsets.UTF_8);
  }

  public static String fromUtf8(InputStream is) {
    try {
      return fromUtf8(ByteStreams.toByteArray(is));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static String split0(String s, String regex) {
    String[] parts = s.split(regex);
    return parts.length != 0 ? parts[0] : s;
  }

  public static String incorrectlyPluralize(int count, String singularForm, String pluralForm) {
    return count == 1 ? singularForm : pluralForm;
  }

  public static boolean areEqual(String one, String two) {
    return Strings.nullToEmpty(one).equals(Strings.nullToEmpty(two));
  }

  public static boolean areEqualIgnoreCase(String one, String two) {
    return Strings.nullToEmpty(one).equalsIgnoreCase(Strings.nullToEmpty(two));
  }

  public static String clip(String s, int len) {
    return (s.length() <= len) ? s : s.substring(0, len);
  }

  public static String elide(String s, int len) {
    if (s.length() < len) {
      return s;
    }

    return s.substring(0, len - 3) + "...";
  }

  public static boolean containsIgnoreCase(String s, String text) {
    return Strings.nullToEmpty(s).toLowerCase().contains(Strings.nullToEmpty(text));
  }

  public static String deaccent(String s) {
    return COMBINING_DIACRITICALS.matcher(Normalizer.normalize(s, Normalizer.Form.NFD)).replaceAll("");
  }

  private MoreStrings() {}

}
