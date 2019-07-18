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
package eu.toop.commander;

import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commander.async.ToopInterfaceListener;
import eu.toop.commander.cli.ToopCommanderCli;
import eu.toop.commander.servlets.RootServlet;
import eu.toop.iface.ToopInterfaceManager;
import eu.toop.iface.servlet.ToDCServlet;
import eu.toop.iface.servlet.ToDPServlet;

/**
 * @author yerlibilgin
 */
public class ToopCommanderMain {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopCommanderMain.class);

  /**
   * Toop commander entry point
   * @param args Commandline arguments
   * @throws Exception in case of error
   */
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
    ToopInterfaceManager.setInterfaceDP(interfaceListener);

    LOGGER.info("Starting server");
    server.start();

    LOGGER.info("Entering CLI mode");

    try {
      ToopCommanderCli toopCommanderCli = new ToopCommanderCli();
      while (toopCommanderCli.readLine()) {
        try {

          Command command = Command.parse(toopCommanderCli.getWords());

          switch (command.getMainCommand()) {
            case "help":
              CommandProcessor.printHelpMessage();
              break;

            case "cat": {
              List<String> fileNames = command.getEmptyParameters();
              fileNames.forEach(fileName -> {
                String trimmedName = fileName.trim();
                if (!trimmedName.isEmpty()) {
                  System.out.println("Contents of " + trimmedName + ":\n");
                  CommandProcessor.printFile(trimmedName);
                }
              });
            }

            break;

            case "id-query":
              CommandProcessor.processIdQuery(command);
              break;

            case "send-dc-request":
            case "send-dp-response":
              CommandProcessor.processDCDPCommand(command);
              break;

            case "run-test":
              CommandProcessor.runTest(command);
              break;

            case "quit":
              CommandProcessor.quit(server);
              break;

            default:
              CommandProcessor.printHelpMessage();
              break;
          }
        } catch (Exception ex) {
          LOGGER.error(ex.getMessage(), ex);
        } finally {
          Thread.sleep(100);
        }
      }
    } catch (UserInterruptException ex) {
      CommandProcessor.quit(server);
    }
  }
}
