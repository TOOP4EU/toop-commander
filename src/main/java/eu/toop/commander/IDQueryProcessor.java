package eu.toop.commander;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;

import eu.toop.commander.dpsearch.EntityType;
import eu.toop.commander.dpsearch.IDType;
import eu.toop.commander.dpsearch.MatchType;
import eu.toop.commander.dpsearch.ResultListType;

/**
 * This class contains provides means to perform or check the id query via the toop connector proxy service to TOOP Directory
 */
public class IDQueryProcessor {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(IDQueryProcessor.class);

  /**
   * Process the id query command.
   *
   * @param command the command
   */
  public static void process(Command command) {
    ValueEnforcer.notNull(command, "Empty command list");

    if (command.hasOption("t")) {
      LOGGER.info("Service availability check");

      String query = CommanderConfig.getConnectorURL() + "/search-dp/AX";

      try {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(query).openConnection();
        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
          throw new IllegalStateException("HTTP status error " + urlConnection.getResponseCode());
        }

        LOGGER.info("Service available");
      } catch (Exception ex) {
        LOGGER.error("Service not available");
        LOGGER.error(ex.getMessage(), ex);
      }
    } else {

      List<String> countryParam = command.getArguments("c");
      if (countryParam == null || countryParam.size() != 1)
        throw new IllegalArgumentException("-c is required with exactly one parameter");

      String countryStr = countryParam.get(0);

      String docTypeStr = "";
      List<String> docTypeParam = command.getArguments("d");
      if (docTypeParam != null) {
        ValueEnforcer.isEqual(docTypeParam.size(), 1, "-d is optional but it requires exactly one parameter");

        try {
          docTypeStr = "/" + URLEncoder.encode(docTypeParam.get(0), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
      }

      String query = CommanderConfig.getConnectorURL() + "/search-dp/" + countryStr + docTypeStr;

      LOGGER.info("URL: " + query);

      try {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(query).openConnection();
        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
          throw new IllegalStateException("HTTP status error " + urlConnection.getResponseCode());
        }

        byte[] allBytes = StreamHelper.getAllBytes(urlConnection.getInputStream());
        String resultStr = new String(allBytes);
        if (command.hasOption("raw")) {
          LOGGER.info(resultStr);
        } else {
          ResultListType resultListType = jaxbUnmarshalFromString(resultStr);
          LOGGER.info("Result List");
          LOGGER.info("Total result count: " + resultListType.getTotalResultCount());
          LOGGER.info("Used result count: " + resultListType.getUsedResultCount());
          LOGGER.info("Match List");
          List<MatchType> matches = resultListType.getMatch();
          for (MatchType match : matches) {
            IDType id = match.getParticipantID();
            LOGGER.info("   ParticipantID [scheme:" + id.getScheme() + ": " + id.getValue());
            LOGGER.info("   Doc Types");
            List<IDType> docTypes = match.getDocTypeID();
            for (IDType docType : docTypes) {
              LOGGER.info("      DocType [scheme: " + docType.getScheme() + "]: " + docType.getValue());
            }
            LOGGER.info("   Entities");
            List<EntityType> entities = match.getEntity();
            for (EntityType entity : entities) {
              LOGGER.info("      Entity: ");
              LOGGER.info("         Name(s): ");
              entity.getName().forEach(nameType -> {
                LOGGER.info("         " + nameType.getValue() + (nameType.getLanguage() != null ? " lang: [" + nameType.getLanguage() + "]" : ""));
              });

              if (entity.getRegDate() != null)
                LOGGER.info("         Registration Date: " + entity.getRegDate());

              LOGGER.info("         Country: " + entity.getCountryCode());

              if (entity.getAdditionalInfo() != null)
                LOGGER.info("         Add. Info: " + entity.getAdditionalInfo());
              if (entity.getGeoInfo() != null)
                LOGGER.info("         Geo Info: " + entity.getGeoInfo());

              LOGGER.info("         Identifiers:");
              List<IDType> identifiers = entity.getIdentifier();
              for (IDType identifer : identifiers) {
                LOGGER.info("            Identifier [scheme: " + identifer.getScheme() + "] " + identifer.getValue());
              }
            }
          }
        }
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
  }

  /**
   * Unmarshal a ResultListType instance from the given string
   * @param str
   * @return
   * @throws javax.xml.bind.JAXBException
   */
  private static ResultListType jaxbUnmarshalFromString(String str)
      throws javax.xml.bind.JAXBException {
    javax.xml.bind.JAXBContext jaxbCtx =
        javax.xml.bind.JAXBContext.newInstance(ResultListType.class.getPackage().getName());
    javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
    JAXBElement ret = (JAXBElement) unmarshaller.unmarshal(new java.io.StringReader(str));
    return (ResultListType) ret.getValue();
  }
}
