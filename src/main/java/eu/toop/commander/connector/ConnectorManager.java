package eu.toop.commander.connector;

import com.helger.asic.SignatureHelper;
import com.helger.security.keystore.EKeyStoreType;
import eu.toop.commander.CommanderConfig;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.commons.exchange.ToopMessageBuilder;
import eu.toop.iface.ToopInterfaceConfig;
import eu.toop.iface.util.HttpClientInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class ConnectorManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorManager.class);

  static final SignatureHelper signatureHelper = new SignatureHelper(EKeyStoreType.JKS,
      CommanderConfig.getKeystore(),
      CommanderConfig.getKeystorePassword(),
      CommanderConfig.getKeyAlias(),
      CommanderConfig.getKeyPassword());


  public static void sendDCRequest(String file) {
    LOGGER.info("Send a DC Request to the endpoint " + CommanderConfig.getConnectorFromDCURL());
    try (FileInputStream inputStream = new FileInputStream(file)) {
      TDETOOPRequestType tdetoopRequestType = ToopMessageBuilder.parseRequestMessage(inputStream);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder.createRequestMessage(tdetoopRequestType, baos, signatureHelper);

      final String aFromDCUrl = CommanderConfig.getConnectorFromDCURL();
      HttpClientInvoker.httpClientCallNoResponse(aFromDCUrl, baos.toByteArray());

    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

  public static void sendDPResponse(String file) {
    LOGGER.info("Send a DP Response to the endpoint " + CommanderConfig.getConnectorFromDPURL());

    try (FileInputStream inputStream = new FileInputStream(file)) {
      TDETOOPResponseType tdetoopResponseType = ToopMessageBuilder.parseResponseMessage(inputStream);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder.createRequestMessage(tdetoopResponseType, baos, signatureHelper);

      final String aFromDPUrl = CommanderConfig.getConnectorFromDPURL();
      HttpClientInvoker.httpClientCallNoResponse(aFromDPUrl, baos.toByteArray());

    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }
}
