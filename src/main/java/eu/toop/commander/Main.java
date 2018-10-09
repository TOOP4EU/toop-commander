package eu.toop.commander;

import eu.toop.commander.async.ToopInterfaceListener;
import eu.toop.commander.cli.ToopCommanderCli;
import eu.toop.commander.servlets.RootServlet;
import eu.toop.iface.ToopInterfaceManager;
import eu.toop.iface.servlet.ToDCServlet;
import eu.toop.iface.servlet.ToDPServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.jline.reader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    servletHandler.addServletWithMapping(ToDCServlet.class, CommanderConfig.getToDcEndpoint());
    LOGGER.info("Registering the to-dp endpoint on " + CommanderConfig.getToDpEndpoint());
    servletHandler.addServletWithMapping(ToDPServlet.class, CommanderConfig.getToDpEndpoint());


    //register endpoint listeners

    ToopInterfaceListener interfaceListener = new ToopInterfaceListener();
    ToopInterfaceManager.setInterfaceDC(interfaceListener);

    LOGGER.info("Starting server");
    server.start();

    LOGGER.info("Entering CLI mode");


    try {

      ToopCommanderCli toopCommanderCli = new ToopCommanderCli();
      while (toopCommanderCli.readLine()) {

        try {

          List<String> commands = toopCommanderCli.getWords();

          if (commands != null && commands.size() > 0) {
            switch (commands.get(0)) {
              case "help":
                DCDPCommandHandler.printHelpMessage();
                break;

              case "cat": {
                for (int i = 1; i < commands.size(); ++i) {
                  String file = commands.get(i).trim();
                  if (!file.isEmpty()) {
                    System.out.println("Contents of " + file + ":\n");
                    DCDPCommandHandler.printFile(file);
                  }
                }
              }

              break;

              case "send-dc-request":
              case "send-dp-response":
                DCDPCommandHandler.processDCDPCommand(commands);
                break;

              case "quit":
                DCDPCommandHandler.quit(server);
                break;

              default:
                DCDPCommandHandler.printHelpMessage();
                break;
            }
          } else {
            DCDPCommandHandler.printHelpMessage();
          }
        } catch (Exception ex) {
          LOGGER.error(ex.getMessage(), ex);
        } finally {
          Thread.sleep(100);
        }
      }
    } catch (UserInterruptException ex) {
      DCDPCommandHandler.quit(server);
    }

  }
}
