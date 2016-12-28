package com.davidbyttow.sfe.storage.entity;

import com.google.common.base.Charsets;

public final class Jsons {
  public static byte[] toBlob(String json) {
    return json.getBytes(Charsets.UTF_8);
  }

  public static String fromBlob(byte[] bytes) {
    return new String(bytes, Charsets.UTF_8);
  }

  public static byte[] defaultValue() {
    return "{}".getBytes(Charsets.UTF_8);
  }

  private Jsons() {}
}
