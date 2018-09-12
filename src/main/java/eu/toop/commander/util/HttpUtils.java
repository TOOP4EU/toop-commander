package eu.toop.commander.util;

import eu.toop.commander.exceptions.ErrorReportingException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
