package eu.toop.commander.async;

import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.exchange.ToopMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet ("/from-dp")
public class FromDPServlet extends HttpServlet {

  private static final Logger s_aLogger = LoggerFactory.getLogger (FromDPServlet.class);

  @Override
  protected void doPost (@Nonnull final HttpServletRequest aHttpServletRequest,
                         @Nonnull final HttpServletResponse aHttpServletResponse) throws ServletException, IOException {
      // Parse ASiC
      final TDETOOPRequestType aRequestMsg = ToopMessageBuilder.parseRequestMessage (aHttpServletRequest.getInputStream ());
      if (aRequestMsg == null) {
        // The message content is invalid
        s_aLogger.error ("The request does not contain an ASiC archive or the ASiC archive does not contain a TOOP Request Message!");
        aHttpServletResponse.setStatus (HttpServletResponse.SC_BAD_REQUEST);
      } else {
        // Call callback
        s_aLogger.info ("The request was successfully received at the FromDP endpoint!");
        //ToopInterfaceManager.getInterfaceDP ().onToopRequest (aRequestMsg);

        // Done - no content
        aHttpServletResponse.setStatus (HttpServletResponse.SC_NO_CONTENT);
    }
  }
}