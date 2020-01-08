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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import eu.toop.commander.cli.ToopCommanderCli;
import eu.toop.commons.util.CliCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;

/**
 * Process the command line input and executes the related services.
 */
public class CommandProcessor {
  /**
   * Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandProcessor.class);

  /**
   * Help message displayed for command input
   */
  private static String helpMessage;

  /**
   * Process the send-dc-request, send-dp-response command.
   *
   * @param command the input command
   */
  public static void processDCDPCommand(CliCommand command) {
    ValueEnforcer.notNull(command, "Empty command list");
    String mainCommand = command.getMainCommand();
    boolean isDCRequest = mainCommand.equals("send-dc-request");

    boolean hasFileOption = command.hasOption("f");
    boolean hasNewOption = command.hasOption("new");

    //the -new and -f options are exclusive
    if (hasFileOption && hasNewOption)
      throw new IllegalStateException("Don't provide both -f and -new options");

    if (hasFileOption) {
      List<String> fileArgs = command.getArguments("f");
      ValueEnforcer.isEqual(fileArgs.size(), 1, "-f option needs exactly one argument");
      if (isDCRequest) {
        ConnectorManager.sendDCRequest(fileArgs.get(0));
      } else {
        ConnectorManager.sendDPResponse(fileArgs.get(0));
      }
    } else if (hasNewOption) {
      List<String> identifierOption = command.getArguments("i");
      List<String> countryOption = command.getArguments("c");
      List<String> metadataFileOption = command.getArguments("m");

      String identifier = identifierOption != null ? identifierOption.get(0) : null;
      String country = countryOption != null ? countryOption.get(0) : null;
      String metadataFile = metadataFileOption != null ? metadataFileOption.get(0) : null;

      if (isDCRequest) {
        ConnectorManager.sendDCRequest(identifier, country, metadataFile);
      } else {
        ConnectorManager.sendDPResponse(identifier, country, metadataFile);
      }
    } else {
      throw new IllegalStateException(mainCommand + " requires one of '-new' or -f 'option'");
    }
  }

  /**
   * Run an automated test based on the given options
   *
   * @param command the command
   */
  public static void runTest(CliCommand command) {
    ValueEnforcer.notNull(command, "Empty command list");
    List<String> file = command.getArguments("f");
    if (file == null || file.size() != 1) {
      throw new IllegalStateException("The -f option is required with exactly one argument");
    }

    List<String> tests = command.getArguments("t");
    ToopTestManager.getInstance().executeTests(file.get(0), tests);
  }

  /**
   * <code>cat</code> the file
   *
   * @param file filename to read and print
   */
  public static void printFile(String file) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line); //don't use logger for this. no need
      }
    } catch (Exception ex) {
      LOGGER.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
    }
  }


  /**
   * Print help message.
   */
  public static void printHelpMessage() {
    if (helpMessage == null) {
      //we are single threaded so no worries
      try (InputStream is = ToopCommanderMain.class.getResourceAsStream("/help.txt")) {
        helpMessage = new String(StreamHelper.getAllBytes(is));
      } catch (Exception ex) {
        helpMessage = "Couldn't load help message";
      }
    }
    System.out.println(helpMessage);
  }

  /**
   * Process and extract the command parameters <code>country</code> and <code>doctype</code> from the command <br>
   * <code>{@value eu.toop.commander.cli.ToopCommanderCli#CMD_SEARCH_DP_BY_COUNTRY} -c &lt;country code&gt; -d &lt;doctype&gt; </code>
   * and perform the {@value eu.toop.commander.cli.ToopCommanderCli#CMD_SEARCH_DP_BY_COUNTRY} query on the toop connector.
   * Then displays the result as both raw and interpreted format
   *
   * @param command the command to be processed
   */
  public static void processDpByCountryQuery(CliCommand command) {
    DPQueryProcessor.processDpSearch(ToopCommanderCli.CMD_SEARCH_DP_BY_COUNTRY, command);
  }

  /**
   * Process and extract the command parameter <code>dpType</code> from the command <br>
   * <code>{@value eu.toop.commander.cli.ToopCommanderCli#CMD_SEARCH_DP_BY_DPTYPE} -d &lt;dpType&gt; </code>
   * and perform the {@value eu.toop.commander.cli.ToopCommanderCli#CMD_SEARCH_DP_BY_DPTYPE}
   * query on the toop connector. Then display the result as both raw and interpreted format
   *
   * @param command the command to be processed
   */
  public static void processDpByDPTypeQuery(CliCommand command) {
    DPQueryProcessor.processDpSearch(ToopCommanderCli.CMD_SEARCH_DP_BY_DPTYPE, command);
  }
}
