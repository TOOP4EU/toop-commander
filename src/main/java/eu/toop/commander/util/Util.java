package eu.toop.commander.util;

import java.security.MessageDigest;

public class Util {
  public static byte[] sha256(byte[] data) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(data);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }


  }

  /**
   * Check if the first bytes of <code>src</code> match <code>probe</code>
   *
   * @param src byte array
   * @param probe byte array
   * @return <code>true</code> if they match, <code>false</code> if not.
   */
  public static boolean matchHeader(byte[] src, byte[] probe) {
    if (src == null || probe == null || src.length < probe.length)
      return false;

    for (int i = 0; i < probe.length; ++i) {
      if (src[i] != probe[i])
        return false;
    }

    return true;
  }
}
