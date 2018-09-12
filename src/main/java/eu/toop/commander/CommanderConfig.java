package eu.toop.commander;

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
  private static final String connectorURL;

  static {
    ConfigParseOptions opt = ConfigParseOptions.defaults();
    opt.setSyntax(ConfigSyntax.CONF);
    Config conf = ConfigFactory.load("toop-commander");

    httpPort = conf.getInt("toop-commander.http.port");
    toDcEndpoint = conf.getString("toop-commander.http.toDcEndpoint");
    toDpEndpoint = conf.getString("toop-commander.http.toDpEndpoint");

    keystore = conf.getString("toop-commander.keystore.file");
    keystorePassword = conf.getString("toop-commander.keystore.password");
    keyAlias = conf.getString("toop-commander.keystore.alias");
    connectorURL = conf.getString("toop-commander.connector.url");
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

  public static String getConnectorURL() {
    return connectorURL;
  }

  public static String getToDcEndpoint() {
    return toDcEndpoint;
  }
  
  public static String getToDpEndpoint() {
    return toDpEndpoint;
  }
}
