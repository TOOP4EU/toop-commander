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
package eu.toop.commander.async;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commander.CommanderConfig;
import eu.toop.commander.ToopMessageCreator;
import eu.toop.commons.dataexchange.v140.TDETOOPRequestType;
import eu.toop.commons.dataexchange.v140.TDETOOPResponseType;
import eu.toop.commons.exchange.ToopMessageBuilder140;
import eu.toop.commons.exchange.ToopRequestWithAttachments140;

/**
 * @author Anton Wiklund
 */
public class ToopInterfaceListenerTest {

  private final static Server server = new Server(CommanderConfig.getConnectorPort ());
  private final static ServletHandler servletHandler = new ServletHandler();
  private static TDETOOPResponseType aResponseMsg; // Populated by the nested class FromDPServlet upon incoming request

  @BeforeAll
  static void initAll() throws Exception {

    server.setHandler(servletHandler);
    servletHandler.addServletWithMapping(FromDPServlet2.class, "/from-dp");
    server.start ();
  }

  @BeforeEach
  void init() {

    ToopInterfaceListenerTest.aResponseMsg = null;
  }

  //@Test
  void toopDataProviderTest() {

    final TDETOOPRequestType aRequest = ToopMessageCreator.createDCRequest (null, null, "metadata.conf");
    final ToopInterfaceListener toopInterfaceListener = new ToopInterfaceListener ();
    try {
      toopInterfaceListener.onToopRequest (new ToopRequestWithAttachments140 (aRequest, null));
      assertNotNull (aResponseMsg);
    } catch (final IllegalStateException ex) {
      // Can happen if the server is not running
    }
  }

  @AfterAll
  static void tearDownAll() {

    try {
      server.stop();
    } catch (Exception e) {
      e.printStackTrace ();
    }
  }

  /**
   * A servlet that mocks the TOOP-Connector's '/from-dp'-endpoint
   */
  @WebServlet ("/from-dp")
  public static class FromDPServlet2 extends HttpServlet {

    private static final Logger s_aLogger = LoggerFactory.getLogger (FromDPServlet2.class);

    @Override
    protected void doPost (@Nonnull final HttpServletRequest aHttpServletRequest,
                           @Nonnull final HttpServletResponse aHttpServletResponse) throws ServletException, IOException {

      // Parse ASiC
      final TDETOOPResponseType responseMsg = ToopMessageBuilder140.parseResponseMessage (aHttpServletRequest.getInputStream (), null);
      ToopInterfaceListenerTest.aResponseMsg = responseMsg;

      if (responseMsg == null) {
        // The message content is invalid
        s_aLogger.error ("The request does not contain an ASiC archive or the ASiC archive does not contain a TOOP Response Message!");
        aHttpServletResponse.setStatus (HttpServletResponse.SC_BAD_REQUEST);
      } else {
        // Done - no content
        s_aLogger.info ("The response was successfully received at the FromDP-endpoint!");
        aHttpServletResponse.setStatus (HttpServletResponse.SC_NO_CONTENT);
      }

    }
  }

}
