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

import com.typesafe.config.Config;
import eu.toop.commander.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * The utility class for reading the toop-commander.conf file.
 *
 * @author yerlibilgin
 */
public class CommanderConfig {
  /**
   * Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(CommanderConfig.class);

  /**
   * Keystore that contains the key for signing the requests and reponses
   */
  private static final String keystore;
  /**
   * Keystore password
   */
  private static final String keystorePassword;
  /**
   * alias of the key that signs the requests and reponses
   */
  private static final String keyAlias;
  /**
   * Key password
   */
  private static final String keyPassword;
  /**
   * Connector dcConnectorBaseURL
   */
  private static final String dcConnectorBaseURL;
  /**
   * fromDCURL for request submission
   */
  private static final String fromDCURL;
  /**
   * fromDPURL for response submission
   */
  private static final String fromDPURL;
  /**
   * timeout for waiting for SubmissionResult and RelayResult
   */
  private static final long testStepWaitTimeout;
  private static boolean cliEnabled;
  private static boolean dcEnabled;
  private static boolean dpEnabled;
  private static int dcPort;
  private static int dpPort;

  static {
    //check if the file toop-commander.conf exists, and load it,
    //otherwise go for classpath resource
    String pathName = "toop-commander.conf";
    URL url = Util.getFileOrResourceAsURL(pathName);

    Config conf = Util.resolveConfiguration(url, true);
    cliEnabled = conf.getBoolean("toop-commander.cliEnabled");
    dcEnabled = conf.getBoolean("toop-commander.dcEnabled");
    dpEnabled = conf.getBoolean("toop-commander.dpEnabled");
    dcPort = conf.getInt("toop-commander.dcPort");
    dpPort = conf.getInt("toop-commander.dpPort");

    keystore = conf.getString("toop-commander.security.keystore");
    keystorePassword = conf.getString("toop-commander.security.keystorePassword");
    keyAlias = conf.getString("toop-commander.security.keyAlias");
    keyPassword = conf.getString("toop-commander.security.keyPassword");

    dcConnectorBaseURL = conf.getString("toop-commander.connector.dcConnectorBaseURL");
    fromDCURL = conf.getString("toop-commander.connector.from-dc-url");
    fromDPURL = conf.getString("toop-commander.connector.from-dp-url");


    if (conf.hasPath("toop-commander.test.testStepWaitTimeout"))
      testStepWaitTimeout = conf.getLong("toop-commander.test.testStepWaitTimeout");
    else
      testStepWaitTimeout = 30000l; //default to 30 seconds


    LOGGER.debug("cliEnabled: " + cliEnabled);
    LOGGER.debug("dcEnabled: " + dcEnabled);
    LOGGER.debug("dpEnabled: " + dpEnabled);
    LOGGER.debug("dcPort: " + dcPort);
    LOGGER.debug("dpPort: " + dpPort);
    LOGGER.debug("keystore: " + keystore);
    LOGGER.debug("keystorePassword: " + keystorePassword);
    LOGGER.debug("keyAlias: " + keyAlias);
    LOGGER.debug("keyPassword: " + keyPassword);
    LOGGER.debug("dcConnectorBaseURL: " + dcConnectorBaseURL);
    LOGGER.debug("fromDCURL: " + fromDCURL);
    LOGGER.debug("fromDPURL: " + fromDPURL);
    LOGGER.debug("testStepWaitTimeout: " + testStepWaitTimeout);
  }

  /**
   * Gets keystore.
   *
   * @return the keystore
   */
  public static String getKeystore() {
    return keystore;
  }

  /**
   * Gets keystore password.
   *
   * @return the keystore password
   */
  public static String getKeystorePassword() {
    return keystorePassword;
  }

  /**
   * Gets key alias.
   *
   * @return the key alias
   */
  public static String getKeyAlias() {
    return keyAlias;
  }

  /**
   * Gets connector url.
   *
   * @return the connector url
   */
  public static String getDcConnectorBaseURL() {
    return dcConnectorBaseURL;
  }

  /**
   * Gets connector from dcurl.
   *
   * @return the connector from dcurl
   */
  public static String getConnectorFromDCURL() {
    return fromDCURL;
  }

  /**
   * Gets connector from dpurl.
   *
   * @return the connector from dpurl
   */
  public static String getConnectorFromDPURL() {
    return fromDPURL;
  }

  /**
   * Gets key password.
   *
   * @return the key password
   */
  public static String getKeyPassword() {
    return keyPassword;
  }

  /**
   * Gets test step wait timeout.
   *
   * @return the test step wait timeout
   */
  public static long getTestStepWaitTimeout() {
    return testStepWaitTimeout;
  }

  public static boolean isCliEnabled() {
    return cliEnabled;
  }

  public static boolean isDcEnabled() {
    return dcEnabled;
  }

  public static boolean isDpEnabled() {
    return dpEnabled;
  }

  public static int getDcPort() {
    return dcPort;
  }

  public static int getDPPort() {
    return dpPort;
  }

}
