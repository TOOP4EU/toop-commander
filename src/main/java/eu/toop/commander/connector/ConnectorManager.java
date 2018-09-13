package eu.toop.commander.connector;

import com.helger.asic.SignatureHelper;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.security.keystore.EKeyStoreType;
import eu.toop.commander.CommanderConfig;
import eu.toop.commander.util.Util;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.commons.exchange.ToopMessageBuilder;
import eu.toop.iface.util.HttpClientInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
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
    LOGGER.info("Send DC request ");
    new ToopRequestMarshaller().sendMessage(file, CommanderConfig.getConnectorFromDCURL());
  }

  public static void sendDPResponse(String file) {
    LOGGER.info("Send DP response ");
    new ToopResponseMarshaller().sendMessage(file, CommanderConfig.getConnectorFromDPURL());
  }


  static abstract class ToopMessageSender {
    public void sendMessage(String file, String url) {
      LOGGER.info("Send file " + file + " to the endpoint " + url);

      try (FileInputStream inputStream = new FileInputStream(file)) {

        byte[] allBytes = StreamHelper.getAllBytes(inputStream);

        //check the first bytes to decide if its an xml file or an asic

        boolean isAsic = Util.matchHeader(allBytes, new byte[]{0x50, 0x4b, 0x03, 0x04});

        if (isAsic) {
          LOGGER.debug("The file is an asic. Send it directly to " + url);
          //the file is already asic, so directly post it
          HttpClientInvoker.httpClientCallNoResponse(url, allBytes);
        } else {
          byte[] asic = convertToAsic(allBytes);

          HttpClientInvoker.httpClientCallNoResponse(CommanderConfig.getConnectorFromDPURL(), asic);
        }
      } catch (Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
      }

    }

    protected abstract byte[] convertToAsic(byte[] allBytes) throws Exception;
  }

  static class ToopResponseMarshaller extends ToopMessageSender {
    @Override
    protected byte[] convertToAsic(byte[] allBytes) throws Exception {
      //assume that it is xml, and error if not an exception will be logged
      JAXBContext jaxbContext = JAXBContext.newInstance(TDETOOPResponseType.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      TDETOOPResponseType tdetoopResponseType = (TDETOOPResponseType) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(allBytes));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder.createResponseMessage(tdetoopResponseType, baos, signatureHelper);
      return baos.toByteArray();
    }
  }


  static class ToopRequestMarshaller extends ToopMessageSender {
    @Override
    protected byte[] convertToAsic(byte[] allBytes) throws Exception {
      //assume that it is xml, and error if not an exception will be logged
      JAXBContext jaxbContext = JAXBContext.newInstance(TDETOOPRequestType.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      TDETOOPRequestType tdeToopRequestType = (TDETOOPRequestType) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(allBytes));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder.createRequestMessage(tdeToopRequestType, baos, signatureHelper);
      return baos.toByteArray();
    }
  }

}
