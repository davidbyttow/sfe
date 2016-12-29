package io.bold.sfe.integrations.pusher;

import com.google.common.base.Throwables;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class PusherSecretSigner {

  private PusherSecretSigner() {}

  public static String sign(String payload, String secret) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(), "SHA256"));
      byte[] digest = mac.doFinal(payload.getBytes("UTF-8"));
      return Hex.encodeHexString(digest);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
