package eu.toop.commander;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;

import java.io.File;

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
  private static final String fromDCURL;
  private static final String fromDPURL;

  private static final String dataProviderSchemeId;
  private static final String dataProviderIdentifier;
  private static final String dataProviderName;
  private static final String dataProviderElectronicAddressIdentifier;
  private static final String dataProviderCountryCode;

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

    fromDCURL = conf.getString("toop-commander.connector.from-dc-url");
    fromDPURL = conf.getString("toop-commander.connector.from-dp-url");

    dataProviderSchemeId = conf.getString("toop-commander.dataProvider.schemeId");
    dataProviderIdentifier = conf.getString("toop-commander.dataProvider.identifier");
    dataProviderName = conf.getString("toop-commander.dataProvider.name");
    dataProviderElectronicAddressIdentifier = conf.getString("toop-commander.dataProvider.electronicAddressIdentifier");
    dataProviderCountryCode = conf.getString("toop-commander.dataProvider.countryCode");
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

  public static String getDataProviderSchemeId () {

    return dataProviderSchemeId;
  }

  public static String getDataProviderIdentifier () {

    return dataProviderIdentifier;
  }

  public static String getDataProviderName () {

    return dataProviderName;
  }

  public static String getDataProviderElectronicAddressIdentifier () {

    return dataProviderElectronicAddressIdentifier;
  }

  public static String getDataProviderCountryCode () {

    return dataProviderCountryCode;
  }
}
