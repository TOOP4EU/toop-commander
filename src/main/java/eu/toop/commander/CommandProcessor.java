/**
 * Copyright (C) 2018-2019 toop.eu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.commander;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
  public static void processDCDPCommand(Command command) {
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
  public static void runTest(Command command) {
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
   * Quit.
   *
   * @param server the server
   * @throws Exception the exception
   */
  public static void quit(Server server) throws Exception {
    LOGGER.info("Stopping the server");
    server.stop();
    server.join();
    System.exit(0);
  }


  /**
   * Print help message.
   */
  public static void printHelpMessage() {
    if (helpMessage == null) {
      //we are single threaded so no worries
      try (InputStream is = Main.class.getResourceAsStream("/help.txt")) {
        helpMessage = new String(StreamHelper.getAllBytes(is));
      } catch (Exception ex) {
        helpMessage = "Couldn't load help message";
      }
    }
    System.out.println(helpMessage);
  }

  /**
   * Process and extract the command parameters <code>country</code> and <code>doctype</code> from the command <br/>
   * <code>id-query -c &lt;country code&gt; -d &lt;doctype&gt; </code>
   * and perform the query on the toop commander. Then displays the result as
   * both raw and interpreted format
   *
   * @param command the command to be processed
   */
  public static void processIdQuery(Command command) {
    IDQueryProcessor.process(command);
  }
}
