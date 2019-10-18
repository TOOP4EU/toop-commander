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

import com.helger.security.keystore.EKeyStoreType;
import eu.toop.commander.cli.ToopCommanderCli;
import eu.toop.commander.util.CommanderUtil;
import eu.toop.commons.util.CliCommand;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author yerlibilgin
 */
public class ToopCommanderMain {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopCommanderMain.class);

  /**
   * Toop commander entry point. Transfer toop-commander.conf to the local directory and then startCommander
   * @param args Commandline arguments
   * @throws Exception in case of error
   */
  public static void main(String[] args) throws Exception {

    //transfer the toop-simulator.conf to local directory so that the user can play with it.
    CommanderUtil.transferResourceToCurrentDirectory("/toop-commander.conf");

    startCommander();
  }

  /**
   * Start the commander
   * @throws Exception
   */
  public static void startCommander() throws Exception {
    ConnectorManager.init(EKeyStoreType.PKCS12,
        CommanderConfig.getKeystore(),
        CommanderConfig.getKeystorePassword(),
        CommanderConfig.getKeyAlias(),
        CommanderConfig.getKeyPassword(),
        CommanderConfig.getConnectorFromDCURL(),
        CommanderConfig.getConnectorFromDPURL());


    //initialize the DC and DP endpoints
    DCDPServerManager.init();

    //check if the logs directory exists, otherwise create it (used for saving the last_response.xml
    new File("logs").mkdirs();

    if(CommanderConfig.isCliEnabled()) {
      LOGGER.info("Entering CLI mode");
      try {
        ToopCommanderCli toopCommanderCli = new ToopCommanderCli();
        while (toopCommanderCli.readLine()) {
          try {

            CliCommand command = CliCommand.parse(toopCommanderCli.getWords(), true);

            switch (command.getMainCommand()) {
              case ToopCommanderCli.CMD_HELP:
                CommandProcessor.printHelpMessage();
                break;

              case "sdr":
                prepareShortcutForDcRequest(command);
                CommandProcessor.processDCDPCommand(command);
                break;

              case ToopCommanderCli.CMD_CAT: {
                List<String> fileNames = command.getEmptyParameters();
                fileNames.forEach(fileName -> {
                  String trimmedName = fileName.trim();
                  if (!trimmedName.isEmpty()) {
                    System.out.println("Contents of " + trimmedName + ":\n");
                    CommandProcessor.printFile(trimmedName);
                  }
                });
              }

              break;

              case ToopCommanderCli.CMD_SEARCH_DP_BY_COUNTRY:
                CommandProcessor.processDpByCountryQuery(command);
                break;

              case ToopCommanderCli.CMD_SEARCH_DP_BY_DPTYPE:
                CommandProcessor.processDpByDPTypeQuery(command);
                break;

              case ToopCommanderCli.CMD_SEND_DC_REQUEST:
              case ToopCommanderCli.CMD_SEND_DP_RESPONSE:
                CommandProcessor.processDCDPCommand(command);
                break;

              case ToopCommanderCli.CMD_RUN_TEST:
                CommandProcessor.runTest(command);
                break;

              case ToopCommanderCli.CMD_QUIT:
                DCDPServerManager.quit();
                System.exit(0);
                break;

              default:
                CommandProcessor.printHelpMessage();
                break;
            }
          } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
          } finally {
            Thread.sleep(100);
          }
        }
      } catch (UserInterruptException ex) {
        DCDPServerManager.quit();
      }

    } else {
      LOGGER.info("CLI mode not enabled. If you want to enable CLI mode, please set toop-commander.cliEnabled=true ");
    }
  }

  public static void prepareShortcutForDcRequest(CliCommand command) {
    try {
      Field field = command.getClass().getDeclaredField("options");
      field.setAccessible(true);
      field.set(command, new HashMap<>());


      field = command.getClass().getDeclaredField("mainCommand");
      field.setAccessible(true);
      field.set(command, "send-dc-request");

      command.getOptions().put("", new ArrayList<>());
      command.getOptions().put("f", Arrays.asList("data/request/TOOPRequest.xml"));
    } catch (NoSuchFieldException nfe) {
    } catch (IllegalAccessException e) {
    }
  }
}
