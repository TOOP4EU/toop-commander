package eu.toop.commander.async;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commander.ConnectorManager;
import eu.toop.commander.ToopMessageCreator;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.iface.IToopInterfaceDC;
import eu.toop.iface.IToopInterfaceDP;

public class ToopInterfaceListener implements IToopInterfaceDC, IToopInterfaceDP {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopInterfaceListener.class);

  @Override
  public void onToopResponse(@Nonnull TDETOOPResponseType aResponse) throws IOException {
    LOGGER.debug("Received a Toop Response");
    LOGGER.debug(aResponse.toString());
  }

  @Override
  public void onToopRequest(@Nonnull TDETOOPRequestType aRequest) throws IOException {
    LOGGER.debug("Received a Toop Request");

    final TDETOOPResponseType aResponse = ToopMessageCreator.createDPResponse (aRequest, "response-metadata.conf");

    ConnectorManager.sendDPResponse (aResponse);
  }
}
