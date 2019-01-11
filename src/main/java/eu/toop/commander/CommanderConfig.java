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

import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;

/**
 * @author yerlibilgin
 */
public class CommanderConfig {

  private static final int httpPort;
  private static final String toDcEndpoint;
  private static final String toDpEndpoint;

  private static final String keystore;
  private static final String keystorePassword;
  private static final String keyAlias;
  private static final String keyPassword;
  private static final String connectorHost;
  private static final int connectorPort;
  private static final String fromDCURL;
  private static final String fromDPURL;
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

    connectorHost = conf.getString("toop-commander.connector.connectorHost");
    connectorPort = conf.getInt("toop-commander.connector.connectorPort");
    fromDCURL = conf.getString("toop-commander.connector.from-dc-url");
    fromDPURL = conf.getString("toop-commander.connector.from-dp-url");

    if (conf.hasPath("toop-commander.test.testStepWaitTimeout"))
      testStepWaitTimeout = conf.getLong("toop-commander.test.testStepWaitTimeout");
    else
      testStepWaitTimeout = 30000l; //default to 30 seconds
  }


  public static int getHttpPort() {
    return httpPort;
  }

  public static String getKeystore() {
    return keystore;
  }

  public static String getKeystorePassword() {
    return keystorePassword;
  }

  public static String getKeyAlias() {
    return keyAlias;
  }

  public static String getConnectorHost() {

    return connectorHost;
  }

  public static int getConnectorPort() {

    return connectorPort;
  }

  public static String getConnectorFromDCURL() {
    return fromDCURL;
  }

  public static String getConnectorFromDPURL() {
    return fromDPURL;
  }

  public static String getToDcEndpoint() {
    return toDcEndpoint;
  }

  public static String getToDpEndpoint() {
    return toDpEndpoint;
  }

  public static String getKeyPassword() {
    return keyPassword;
  }

  public static long getTestStepWaitTimeout() {
    return testStepWaitTimeout;
  }
}
