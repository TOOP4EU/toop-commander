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
