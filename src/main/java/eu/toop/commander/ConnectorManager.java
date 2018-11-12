package eu.toop.commander;

import com.helger.asic.SignatureHelper;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.security.keystore.EKeyStoreType;
import eu.toop.commander.util.Util;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.commons.exchange.ToopMessageBuilder;
import eu.toop.commons.jaxb.ToopReader;
import eu.toop.iface.util.HttpClientInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;

public class ConnectorManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorManager.class);

  static final SignatureHelper signatureHelper = new SignatureHelper(EKeyStoreType.JKS,
      CommanderConfig.getKeystore(),
      CommanderConfig.getKeystorePassword(),
      CommanderConfig.getKeyAlias(),
      CommanderConfig.getKeyPassword());

  public static final String CONNECTOR_FROM_DCURL = CommanderConfig.getConnectorFromDCURL();
  public static final String CONNECTOR_FROM_DPURL = CommanderConfig.getConnectorFromDPURL();


  public static void sendDCRequest(String file) {
    LOGGER.info("Send DC request ");
    new ToopRequestMarshaller().sendMessage(file, CONNECTOR_FROM_DCURL);
  }

  public static void sendDPResponse(String file) {
    LOGGER.info("Send DP response ");
    new ToopResponseMarshaller().sendMessage(file, CONNECTOR_FROM_DPURL);
  }

  public static void sendDPResponse(final TDETOOPResponseType tdeToopResponseType) {
    LOGGER.info("Send DP response ");

    //log the last request to file
    try {
      Files.write(new File("logs/last_response.xml").toPath(), ToopMessageCreator.serializeResponse(tdeToopResponseType));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      LOGGER.debug("Create asic");
      ToopMessageBuilder.createResponseMessageAsic(tdeToopResponseType, baos, signatureHelper);
      LOGGER.debug("Send the response to " + CONNECTOR_FROM_DPURL);
      HttpClientInvoker.httpClientCallNoResponse(CONNECTOR_FROM_DPURL, baos.toByteArray());
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }


  //Create a DP response from scratch and send it
  public static void sendDPResponse(String identifier, String country, String metadataFile) {
    LOGGER.info("Send DP Response Identifier: " + identifier + " Country: " + country + " metadata file: " + metadataFile);

    TDETOOPResponseType tdetoopResponseType = ToopMessageCreator.createDPResponse(identifier, country, metadataFile);

    sendDPResponse(tdetoopResponseType);
  }


  //Create a DC request from scratch and send it
  public static void sendDCRequest(String identifier, String country, String metadataFile) {
    LOGGER.info("Send DC Request Identifier: " + identifier + " Country: " + country + " metadata file: " + metadataFile);


    TDETOOPRequestType tdetoopRequestType = ToopMessageCreator.createDCRequest(identifier, country, metadataFile);

    //log the last request to file
    try {
      Files.write(new File("logs/last_request.xml").toPath(), ToopMessageCreator.serializeRequest(tdetoopRequestType));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      LOGGER.debug("Create asic");
      ToopMessageBuilder.createRequestMessageAsic(tdetoopRequestType, baos, signatureHelper);
      LOGGER.debug("Send the request to " + CONNECTOR_FROM_DCURL);
      byte[] aDataToSend = baos.toByteArray();
      HttpClientInvoker.httpClientCallNoResponse(CONNECTOR_FROM_DCURL, aDataToSend);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }

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

          HttpClientInvoker.httpClientCallNoResponse(url, asic);
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
      TDETOOPResponseType tdetoopResponseType = ToopReader.response().read(allBytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder.createRequestMessageAsic(tdetoopResponseType, baos, signatureHelper);
      return baos.toByteArray();
    }
  }


  static class ToopRequestMarshaller extends ToopMessageSender {
    @Override
    protected byte[] convertToAsic(byte[] allBytes) throws Exception {
      //assume that it is xml, and error if not an exception will be logged
      TDETOOPRequestType tdeToopRequestType = ToopReader.request().read(allBytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder.createRequestMessageAsic(tdeToopRequestType, baos, signatureHelper);
      return baos.toByteArray();

    }
  }

}
