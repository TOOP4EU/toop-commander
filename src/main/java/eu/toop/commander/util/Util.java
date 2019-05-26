/**
 * Copyright (C) 2018-2019 toop.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.commander.util;

import java.security.MessageDigest;

/**
 * A class that contains utility functions
 */
public class Util {
  /**
   * Returns the SHA 256 hash of the input data
   * @param data Data to digets
   * @return Digest bytes
   */
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
