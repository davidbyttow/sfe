package com.simplethingsllc.framework.images;

public class Images {

  private static class Type {
    Type(String contentType, byte[] prefix) {
      this.contentType = contentType;
      this.prefix = prefix;
    }
    String contentType;
    byte[] prefix;
  }

  private static Type[] TYPES = new Type[]{
    new Type("image/png", new byte[]{(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47}),
    new Type("image/jpeg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF}),
  };

  public static String detectContentType(byte[] bytes) {
    for (Type type : TYPES) {
      byte[] prefix = type.prefix;
      if (hasPrefix(prefix, bytes)) {
        return type.contentType;
      }
    }
    return "application/octet-stream";
  }

  public static boolean hasPrefix(byte[] prefix, byte[] bytes) {
    if (bytes.length < prefix.length) {
      return false;
    }
    for (int i = 0; i < prefix.length; ++i) {
      if (bytes[i] != prefix[i]) {
        return false;
      }
    }
    return true;
  }
}
