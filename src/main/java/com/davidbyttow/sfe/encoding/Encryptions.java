package com.davidbyttow.sfe.encoding;

import com.google.common.base.Throwables;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public final class Encryptions {

  public static byte[] aesEncrypt(String text, String key) {
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      return cipher.doFinal(text.getBytes());
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static String aesDecrypt(byte[] bytes, String key) {
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, aesKey);
      return new String(cipher.doFinal(bytes));
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private Encryptions() {}
}
