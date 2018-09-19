package eu.toop.commander;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Anton Wiklund
 */
public class CommanderConfigTest {

  private static final Logger s_aLogger = LoggerFactory.getLogger (CommanderConfigTest.class);

  @Test
  void commanderConfigTest() {

    assertNotEquals (CommanderConfig.getHttpPort (), 0);
    assertNotNull (CommanderConfig.getKeystore ());
    assertNotNull (CommanderConfig.getKeystorePassword ());
    assertNotNull (CommanderConfig.getKeyAlias ());
    assertNotNull (CommanderConfig.getConnectorFromDCURL ());
    assertNotNull (CommanderConfig.getConnectorFromDPURL ());
    assertNotNull (CommanderConfig.getToDcEndpoint ());
    assertNotNull (CommanderConfig.getToDpEndpoint ());
    assertNotNull (CommanderConfig.getKeyPassword ());
  }
}
