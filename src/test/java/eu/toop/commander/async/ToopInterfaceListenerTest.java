package eu.toop.commander.async;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import eu.toop.commander.CommanderConfig;
import eu.toop.commander.ToopMessageCreator;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.commons.exchange.ToopMessageBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Anton Wiklund
 */
public class ToopInterfaceListenerTest {

  private static final Logger s_aLogger = LoggerFactory.getLogger (ToopInterfaceListenerTest.class);
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

  @Test
  void toopDataProviderTest() throws IOException {

    final TDETOOPRequestType aRequest = ToopMessageCreator.createDCRequest (null, null, "metadata.conf");
    final ToopInterfaceListener toopInterfaceListener = new ToopInterfaceListener ();
    toopInterfaceListener.onToopRequest (aRequest);

    assertNotNull (aResponseMsg);
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
      final TDETOOPResponseType aResponseMsg = ToopMessageBuilder.parseResponseMessage (aHttpServletRequest.getInputStream ());
      ToopInterfaceListenerTest.aResponseMsg = aResponseMsg;

      if (aResponseMsg == null) {
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