package eu.toop.commander.servlets;

import eu.toop.commander.CommanderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yerlibilgin
 */
public class RootServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToDpServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getOutputStream().print("<html><head><title>Toop Commander</title></head><body><h2>Toop Commander is UP and Running</h2>" +
        "<h3>Endpoints</h3><br/>" +
        "<ul>" +
        "<li> DC: <a href='" + CommanderConfig.getToDcEndpoint() + "'>" + CommanderConfig.getToDcEndpoint() +  "</a></li>" +
        "<li> DP: <a href='" + CommanderConfig.getToDpEndpoint() + "'>" + CommanderConfig.getToDpEndpoint() +  "</a></li>" +
        "</body></html>");
    resp.getOutputStream().flush();
  }
}