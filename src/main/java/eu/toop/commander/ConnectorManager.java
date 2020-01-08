/**
 * Copyright (C) 2018-2020 toop.eu
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

import java.io.*;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.asic.SignatureHelper;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.security.keystore.EKeyStoreType;

import eu.toop.commander.util.Util;
import eu.toop.commons.dataexchange.v140.TDETOOPRequestType;
import eu.toop.commons.dataexchange.v140.TDETOOPResponseType;
import eu.toop.commons.error.ToopErrorException;
import eu.toop.commons.exchange.AsicWriteEntry;
import eu.toop.commons.exchange.ToopMessageBuilder140;
import eu.toop.commons.jaxb.ToopReader;
import eu.toop.iface.util.HttpClientInvoker;

/**
 * The type Connector manager.
 */
public class ConnectorManager {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorManager.class);
  /**
   * The Signature helper.
   */
  private static SignatureHelper signatureHelper;

  /**
   * The constant CONNECTOR_FROM_DCURL.
   */
  private static String CONNECTOR_FROM_DCURL;
  /**
   * The constant CONNECTOR_FROM_DPURL.
   */
  private static String CONNECTOR_FROM_DPURL;


  public static void init(EKeyStoreType type, String keyStore, String storePassword, String keyALias, String keyPassword, String fromDCUrl, String fromDPUrl){
    signatureHelper = new SignatureHelper(type, keyStore, storePassword, keyALias, keyPassword);
    CONNECTOR_FROM_DCURL = fromDCUrl;
    CONNECTOR_FROM_DPURL = fromDPUrl;
  }

  /**
   * Send dc request.
   *
   * @param file the file
   */
  public static void sendDCRequest(String file) {
    LOGGER.info("Send DC request ");
    new ToopRequestMarshaller().sendMessage(file, CONNECTOR_FROM_DCURL);
  }

  /**
   * Send dp response.
   *
   * @param file the file
   */
  public static void sendDPResponse(String file) {
    LOGGER.info("Send DP response ");
    new ToopResponseMarshaller().sendMessage(file, CONNECTOR_FROM_DPURL);
  }

  /**
   * Send dp response.
   *
   * @param tdeToopResponseType the tde toop response type
   */
  public static void sendDPResponse(final TDETOOPResponseType tdeToopResponseType) {
    sendDPResponse(tdeToopResponseType, null);
  }

  public static void sendDPResponse(TDETOOPResponseType tdeToopResponseType, Iterable<AsicWriteEntry> attachments) {
    LOGGER.info("Send DP response ");

    //log the last request to file
    try {
      Files.write(new File("logs/last_response.xml").toPath(), ToopMessageCreator.serializeResponse(tdeToopResponseType));
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
      throw new UncheckedIOException(e);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      LOGGER.debug("Create asic");
      ToopMessageBuilder140.createResponseMessageAsic(tdeToopResponseType, baos, signatureHelper, attachments);
      LOGGER.debug("Send the response to " + CONNECTOR_FROM_DPURL);
      HttpClientInvoker.httpClientCallNoResponse(CONNECTOR_FROM_DPURL, baos.toByteArray());
    } catch (ToopErrorException | IOException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }


  /**
   * Send dp response.
   *
   * @param identifier   the identifier
   * @param country      the country
   * @param metadataFile the metadata file
   */
//Create a DP response from scratch and send it
  public static void sendDPResponse(String identifier, String country, String metadataFile) {
    LOGGER.info("Send DP Response Identifier: " + identifier + " Country: " + country + " metadata file: " + metadataFile);

    TDETOOPResponseType tdetoopResponseType = ToopMessageCreator.createDPResponse(identifier, country, metadataFile);

    sendDPResponse(tdetoopResponseType);
  }

  /**
   * Send dc request.
   *
   * @param identifier   the identifier
   * @param country      the country
   * @param metadataFile the metadata file
   */
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
      ToopMessageBuilder140.createRequestMessageAsic(tdetoopRequestType, baos, signatureHelper);
      LOGGER.debug("Send the request to " + CONNECTOR_FROM_DCURL);
      byte[] aDataToSend = baos.toByteArray();
      HttpClientInvoker.httpClientCallNoResponse(CONNECTOR_FROM_DCURL, aDataToSend);
    } catch (ToopErrorException | IOException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }

  }

  /**
   * The type Toop message sender.
   */
  static abstract class ToopMessageSender {
    /**
     * Send message.
     *
     * @param file the file
     * @param url  the url
     */
    public void sendMessage(String file, String url) {
      LOGGER.info("Send file/cp resource" + file + " to the endpoint " + url);

      try (InputStream inputStream = Util.loadFileOrResourceStream(file)) {

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
        throw new IllegalStateException(ex.getMessage(), ex);
      }

    }

    /**
     * Convert to asic byte [ ].
     *
     * @param allBytes the all bytes
     * @return the byte [ ]
     * @throws Exception the exception
     */
    protected abstract byte[] convertToAsic(byte[] allBytes) throws Exception;
  }

  /**
   * The type Toop response marshaller.
   */
  static class ToopResponseMarshaller extends ToopMessageSender {
    @Override
    protected byte[] convertToAsic(byte[] allBytes) throws Exception {
      //assume that it is xml, and error if not an exception will be logged
      TDETOOPResponseType tdetoopResponseType = ToopReader.response140().read(allBytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder140.createRequestMessageAsic(tdetoopResponseType, baos, signatureHelper);
      return baos.toByteArray();
    }
  }


  /**
   * The type Toop request marshaller.
   */
  static class ToopRequestMarshaller extends ToopMessageSender {
    @Override
    protected byte[] convertToAsic(byte[] allBytes) throws Exception {
      //assume that it is xml, and error if not an exception will be logged
      TDETOOPRequestType tdeToopRequestType = ToopReader.request140().read(allBytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ToopMessageBuilder140.createRequestMessageAsic(tdeToopRequestType, baos, signatureHelper);
      return baos.toByteArray();

    }
  }

}
