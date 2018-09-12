package eu.toop.commander.util;

import com.helger.commons.io.stream.StreamHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
  public static byte[] sha256(byte[] data) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(data);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }


  }
}
