package io.bold.sfe.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class MorePreconditions {

  public static void checkAllNotNull(Object ...objects) {
    for (Object object : objects) {
      Preconditions.checkNotNull(object);
    }
  }

  public static Object checkNotNullOrEmpty(Object object) {
    Preconditions.checkNotNull(object);
    if (object instanceof String) {
      String s = (String) object;
      Preconditions.checkArgument(!s.isEmpty());
    }
    return object;
  }

  public static String checkNotNullOrEmpty(String s) {
    return Preconditions.checkNotNull(Strings.emptyToNull(s));
  }

  public static void checkAllNotNullOrEmpty(Object ...objects) {
    for (Object object : objects) {
      checkNotNullOrEmpty(object);
    }
  }

  private MorePreconditions() {}
}
