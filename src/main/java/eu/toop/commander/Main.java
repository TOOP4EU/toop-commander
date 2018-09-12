package eu.toop.commander;

import eu.toop.commander.servlets.RootServlet;
import eu.toop.commander.servlets.ToDcServlet;
import eu.toop.commander.servlets.ToDpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yerlibilgin
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    int port = CommanderConfig.getHttpPort();

    if (args.length >= 1) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException ex) {
        LOGGER.error("Invalid port  " + args[0]);
        throw new IllegalStateException("Invalid port  " + args[0]);
      }
    }
    LOGGER.info("Starting toop-commander on port " + port);
    Server server = new Server(port);

    ServletHandler servletHandler = new ServletHandler();
    server.setHandler(servletHandler);

    LOGGER.info("Registering root servlet");
    servletHandler.addServletWithMapping(RootServlet.class, "/");
    LOGGER.info("Registering the to-dc endpoint on " + CommanderConfig.getToDcEndpoint());
    servletHandler.addServletWithMapping(ToDcServlet.class, CommanderConfig.getToDcEndpoint());
    LOGGER.info("Registering the to-dp endpoint on " + CommanderConfig.getToDpEndpoint());
    servletHandler.addServletWithMapping(ToDpServlet.class, CommanderConfig.getToDpEndpoint());

    LOGGER.info("Starting server");
    server.start();
    server.join();
  }
}
