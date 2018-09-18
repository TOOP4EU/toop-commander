package eu.toop.commander.async;

import eu.toop.commander.connector.ConnectorManager;
import eu.toop.commons.dataexchange.TDEAddressType;
import eu.toop.commons.dataexchange.TDEDataProviderType;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.commons.jaxb.ToopXSDHelper;
import eu.toop.iface.IToopInterfaceDC;
import eu.toop.iface.IToopInterfaceDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.IOException;

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

    // Convert the TOOP Request to a TOOP Response
    final TDETOOPResponseType aResponse = new TDETOOPResponseType ();
    aRequest.cloneTo (aResponse);

    // Required for response
    final TDEDataProviderType p = new TDEDataProviderType ();
    p.setDPIdentifier (ToopXSDHelper.createIdentifier ("iso6523-actorid-upis", "9999:test-dp"));
    p.setDPName (ToopXSDHelper.createText ("test-dp"));
    p.setDPElectronicAddressIdentifier (ToopXSDHelper.createIdentifier ("test-dp@register.example.org"));
    final TDEAddressType pa = new TDEAddressType ();
    pa.setCountryCode (ToopXSDHelper.createCode ("XX"));
    p.setDPLegalAddress (pa);
    aResponse.setDataProvider (p);

    ConnectorManager.sendDPResponse (aResponse);
  }
}
