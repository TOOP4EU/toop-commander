/**
 * Copyright (C) 2018 toop.eu
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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import eu.toop.commander.exceptions.ErrorReportingException;

public class HttpUtils {
  public static void sendError(String errorString, int statusCode, HttpServletResponse resp) {
    resp.setStatus(statusCode);

    try {
      resp.getOutputStream().write(errorString.getBytes());
      resp.getOutputStream().flush();
    } catch (IOException e) {
      throw new ErrorReportingException(e);
    }
  }
}
