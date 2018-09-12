package eu.toop.commander.servlets;

import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.commons.exchange.ToopMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static eu.toop.commander.util.HttpUtils.sendError;

/**
 * @author yerlibilgin
 */
public class ToDcServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToDpServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getOutputStream().print("<html><head><title>Toop Commander</title></head><body><h2>to-dc endpoint</h2></body></html>");
    resp.getOutputStream().flush();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    //read the content type
    LOGGER.info("Received a post message");
    String contentType = req.getContentType();
    LOGGER.debug("Content type " + contentType);

    try {
      //just parse the message for now
      LOGGER.debug("Parse toop response");
      TDETOOPResponseType tdeToopResponseType = ToopMessageBuilder.parseResponseMessage(req.getInputStream());
      LOGGER.debug(tdeToopResponseType.getDataRequestSubject().toString());

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      sendError("Couldn't parse XML", HttpServletResponse.SC_BAD_REQUEST, resp);
    }
  }
}