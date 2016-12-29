package io.bold.sfe.common;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hashes {

  private static final Logger log = LoggerFactory.getLogger(Hashes.class);

  public static String md5(String input) {
    return md5(input.getBytes(Charsets.UTF_8));
  }

  public static String md5(byte[] bytes) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(bytes, 0, bytes.length);
      return Hex.encodeHexString(md.digest());
    } catch (NoSuchAlgorithmException e) {
      throw Throwables.propagate(e);
    }
  }

  public static String rot13(String input) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      if (c >= 'a' && c <= 'm') {
        c += 13;
      } else if (c >= 'A' && c <= 'M') {
        c += 13;
      } else if (c >= 'n' && c <= 'z') {
        c -= 13;
      } else if (c >= 'N' && c <= 'Z') {
        c -= 13;
      }
      out.append(c);
    }
    return out.toString();
  }

  private Hashes() {}
}
