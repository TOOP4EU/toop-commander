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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.pd.searchapi.PDSearchAPIReader;
import com.helger.pd.searchapi.v1.EntityType;
import com.helger.pd.searchapi.v1.IDType;
import com.helger.pd.searchapi.v1.MatchType;
import com.helger.pd.searchapi.v1.ResultListType;
import eu.toop.commander.cli.ToopCommanderCli;
import eu.toop.commons.util.CliCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * This class contains provides means to perform or check the id query via the toop connector proxy service to TOOP Directory
 */
public class DPQueryProcessor {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(DPQueryProcessor.class);


  /**
   * Process the id query command.
   *
   * @param mainCommand either {@value eu.toop.commander.cli.ToopCommanderCli#CMD_SEARCH_DP_BY_COUNTRY} or
   *                    {@value eu.toop.commander.cli.ToopCommanderCli#CMD_SEARCH_DP_BY_DPTYPE}
   * @param command the command
   */
  public static void processDpSearch(String mainCommand, CliCommand command) {
    ValueEnforcer.notNull(mainCommand, "Main command should be valid");
    ValueEnforcer.notNull(command, "Empty command list");


    String query = CommanderConfig.getDcConnectorBaseURL() + "/" + mainCommand;
    if (command.hasOption("t")) {
      LOGGER.info("Service availability check");

      query += "/test";

      LOGGER.info("URL: " + query);
      try {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(query).openConnection();
        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
          throw new IllegalStateException("HTTP status error " + urlConnection.getResponseCode());
        }

        LOGGER.info("Service available");
      } catch (MalformedURLException ex){
        LOGGER.error("Invalid URL");
        LOGGER.error(ex.getMessage(), ex);
      } catch (Exception ex) {
        LOGGER.error("Service not available");
        LOGGER.error(ex.getMessage(), ex);
      }
    } else {
      if(mainCommand.equals(ToopCommanderCli.CMD_SEARCH_DP_BY_COUNTRY)) {
        List<String> countryParam = command.getArguments("c");
        if (countryParam == null || countryParam.size() != 1)
          throw new IllegalArgumentException("-c is required with exactly one parameter");

        String countryStr = "/" + countryParam.get(0);

        String docTypeStr = "";
        List<String> docTypeParam = command.getArguments("d");
        if (docTypeParam != null) {
          ValueEnforcer.isEqual(docTypeParam.size(), 1, "-d is optional but it requires exactly one parameter");

          try {
            docTypeStr = "/" + URLEncoder.encode(docTypeParam.get(0), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Couldn't prepare URL for the query " + command.toString(), e);
          }
        }

        query += countryStr + docTypeStr;
      } else if (mainCommand.equals(ToopCommanderCli.CMD_SEARCH_DP_BY_DPTYPE)){
        List<String> dpTypeParam = command.getArguments("d");

        String dpTypeStr = "";
        if (dpTypeParam != null) {
          ValueEnforcer.isEqual(dpTypeParam.size(), 1, "-d is requires the `dyType` value");
          try {
            dpTypeStr = "/" + URLEncoder.encode(dpTypeParam.get(0), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Couldn't prepare URL for the query " + command.toString(), e);
          }

          query += dpTypeStr;
        } else {
          throw new IllegalArgumentException("You have to provide `dpType` parameter for the command " + mainCommand);
        }

      }

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
   * @param str string
   * @return <code>null</code> on error
   */
  private static ResultListType jaxbUnmarshalFromString(String str)  {
    return PDSearchAPIReader.resultListV1 ().read (str);
  }
}
