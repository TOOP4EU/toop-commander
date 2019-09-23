/**
 * Copyright (C) 2018-2019 toop.eu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.commander;

import eu.toop.commander.async.ToopInterfaceListener;
import eu.toop.iface.ToopInterfaceManager;
import eu.toop.iface.servlet.ToDCServlet;
import eu.toop.iface.servlet.ToDPServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yerlibilgin
 */
public class DCDPServerManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(DCDPServerManager.class);

  private static Server dcServer;
  private static Server dpServer;

  public static void init() throws Exception {
    if (CommanderConfig.isDcEnabled())
      dcServer = prepareDCInterface();
    else
      LOGGER.warn("DC Endpoint not enabled");

    if (CommanderConfig.isDpEnabled())
      dpServer = prepareDPInterface();
    else
      LOGGER.warn("DP Endpoint not enabled");
    //register endpoint listeners

    ToopInterfaceListener interfaceListener = new ToopInterfaceListener();
    ToopInterfaceManager.setInterfaceDC(interfaceListener);
    ToopInterfaceManager.setInterfaceDP(interfaceListener);
  }

  /**
   * Stop the DC and DP endpoints if they are up
   */
  public static void quit() {
    LOGGER.info("Stopping servers");
    try {
      dcServer.stop();
      dcServer.join();
    } catch (Exception ex) {
    }
    try {
      dpServer.stop();
      dpServer.join();
    } catch (Exception ex) {
    }
  }


  private static Server prepareDCInterface() throws Exception {
    int httpPort = CommanderConfig.getDcPort();

    LOGGER.info("Starting DC interface on port " + httpPort);
    Server server = new Server(httpPort);

    ServletHandler servletHandler = new ServletHandler();
    server.setHandler(servletHandler);
    LOGGER.info("Registering the to-dc endpoint on /to-dc");
    servletHandler.addServletWithMapping(ToDCServlet.class, "/to-dc");

    server.start();
    LOGGER.info("Started DC Server");
    return server;
  }


  private static Server prepareDPInterface() throws Exception {
    int httpPort = CommanderConfig.getDPPort();

    LOGGER.info("Starting DP interface on port " + httpPort);
    Server server = new Server(httpPort);

    ServletHandler servletHandler = new ServletHandler();
    server.setHandler(servletHandler);

    LOGGER.info("Registering the to-dp endpoint on /to-dp");
    servletHandler.addServletWithMapping(ToDPServlet.class, "/to-dp");

    server.start();
    LOGGER.info("Started DP Server");
    return server;
  }
}
