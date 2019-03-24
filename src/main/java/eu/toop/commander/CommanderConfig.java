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

import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;

/**
 * The utility class for reading the toop-commander.conf file.
 *
 * @author yerlibilgin
 */
public class CommanderConfig {
  /**
   * HTTP Port
   */
  private static final int httpPort;
  /**
   * TO DC Endpoint
   */
  private static final String toDcEndpoint;
  /**
   * TO DP Endpoint
   */
  private static final String toDpEndpoint;

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
   * Connector URL
   */
  private static final String connectorURL;
  /**
   * Connector Port
   */
  private static final int connectorPort;
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


  static {
    ConfigParseOptions opt = ConfigParseOptions.defaults();
    opt.setSyntax(ConfigSyntax.CONF);
    Config conf = ConfigFactory.parseFile(new File("./toop-commander.conf")).resolve();

    httpPort = conf.getInt("toop-commander.http.port");
    toDcEndpoint = conf.getString("toop-commander.http.toDcEndpoint");
    toDpEndpoint = conf.getString("toop-commander.http.toDpEndpoint");

    keystore = conf.getString("toop-commander.security.keystore");
    keystorePassword = conf.getString("toop-commander.security.keystorePassword");
    keyAlias = conf.getString("toop-commander.security.keyAlias");
    keyPassword = conf.getString("toop-commander.security.keyPassword");

    connectorURL = conf.getString("toop-commander.connector.connectorURL");
    connectorPort = conf.getInt("toop-commander.connector.connectorPort");
    fromDCURL = conf.getString("toop-commander.connector.from-dc-url");
    fromDPURL = conf.getString("toop-commander.connector.from-dp-url");

    if (conf.hasPath("toop-commander.test.testStepWaitTimeout"))
      testStepWaitTimeout = conf.getLong("toop-commander.test.testStepWaitTimeout");
    else
      testStepWaitTimeout = 30000l; //default to 30 seconds
  }


  /**
   * Gets http port.
   *
   * @return the http port
   */
  public static int getHttpPort() {
    return httpPort;
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
  public static String getConnectorURL() {
    return connectorURL;
  }

  /**
   * Gets connector port.
   *
   * @return the connector port
   */
  public static int getConnectorPort() {

    return connectorPort;
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
   * Gets to dc endpoint.
   *
   * @return the to dc endpoint
   */
  public static String getToDcEndpoint() {
    return toDcEndpoint;
  }

  /**
   * Gets to dp endpoint.
   *
   * @return the to dp endpoint
   */
  public static String getToDpEndpoint() {
    return toDpEndpoint;
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
}
