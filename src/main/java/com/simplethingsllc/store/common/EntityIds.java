package com.simplethingsllc.store.common;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Arrays;

public final class EntityIds {
  private static final Joiner JOINER = Joiner.on(':');

  public static String fromParts(String... parts) {
    return fromParts(Arrays.asList(parts));
  }

  public static String fromParts(Iterable<String> parts) {
    parts.forEach(s -> Preconditions.checkArgument(!Strings.isNullOrEmpty(s)));
    return JOINER.join(parts);
  }

  private EntityIds() {}
}
