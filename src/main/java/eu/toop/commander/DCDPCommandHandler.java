/**
 * Copyright (C) 2018 toop.eu
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;

public class DCDPCommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(DCDPCommandHandler.class);

  private static String helpMessage;


  public static void processDCDPCommand(List<String> commands) {
    ValueEnforcer.notEmpty(commands, "Empty command list");
    String command = commands.get(0);
    boolean isDCRequest = command.equals("send-dc-request");


    if (commands.size() < 3)
      throw new IllegalArgumentException(command + " requires at least 3 arguments");

    String file[] = getOption(commands, "-f", 2);
    if (file != null) {
      if (isDCRequest) {
        ConnectorManager.sendDCRequest(file[1]);
      } else {
        ConnectorManager.sendDPResponse(file[1]);
      }
      return;
    }


    if (getOption(commands, "-new", 1) != null) {
      String[] identifierOption = getOption(commands, "-i", 2);
      String[] countryOption = getOption(commands, "-c", 2);
      String[] metadataFileOption = getOption(commands, "-m", 2);

      String identifier = identifierOption != null ? identifierOption[1] : null;
      String country = countryOption != null ? countryOption[1] : null;
      String metadataFile = metadataFileOption != null ? metadataFileOption[1] : null;


      if (isDCRequest) {
        ConnectorManager.sendDCRequest(identifier, country, metadataFile);
      } else {
        ConnectorManager.sendDPResponse(identifier, country, metadataFile);
      }
    }

  }

  public static void runTest(List<String> commands) {

    ValueEnforcer.notEmpty(commands, "Empty command list");
    String command = commands.get(0);

    if (commands.size() < 3)
      throw new IllegalArgumentException(command + " requires at least 3 arguments");

    String file[] = getOption(commands, "-f", 2);
    String tests[] = getOption(commands, "-t", 2);

    if (file != null) {

      ToopTestManager.getInstance ().executeTests (file[1], tests);
    }
  }

  private static String[] getOption(List<String> commands, String option, int expectedLength) {
    ValueEnforcer.isGE0(expectedLength, "expected length");

    String[] result;
    int size = commands.size();
    for (int i = 0; i < size; ++i) {
      String current = commands.get(i);

      if (current.equals(option)) {
        if (expectedLength == 1) {

          //check if the next argument starts with - (it must have a dash, because this option
          //is expected to have no value
          if (i != size - 1 && !commands.get(i + 1).startsWith("-")) {
            throw new IllegalArgumentException(option + " does not take parameters");
          }

          return new String[]{current};
        } else {
          int count = 1;
          List<String> options = new ArrayList<>(expectedLength);
          options.add(current);
          //we have a hit. traverse until no string starting with dash (-)
          for (int j = i + 1; j < size; ++j) {
            String tmp = commands.get(j);
            if (tmp.startsWith("-") && count < expectedLength) {
              throw new IllegalArgumentException(option + " expects " + (expectedLength - 1) + " parameters");
            }

            count++;
            options.add(tmp);
          }

          if (count < expectedLength) {
            throw new IllegalArgumentException(option + " expects " + (expectedLength - 1) + " parameters");
          }

          return options.toArray(new String[0]);
        }
      }
    }

    return null;
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

  public static void quit(Server server) throws Exception {
    LOGGER.info("Stopping the server");
    server.stop();
    server.join();
    System.exit(0);
  }


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
}
