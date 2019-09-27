/**
 * Copyright (C) 2018-2019 toop.eu
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class CommandTest {
  @Test
  public void parseEmptyCommandList () {
    try {
      Command.parse (new ArrayList<> (), true);
      fail ();
    } catch (IllegalArgumentException ex) {
      // Expected
    }
  }

  @Test
  public void parseOneCommand () {
    Command singleCommand = Command.parse (Arrays.asList ("singleCommand"), true);

    assertEquals ("singleCommand", singleCommand.getMainCommand ());
    assertNull (singleCommand.getOptions ());
  }

  @Test
  public void parseComplexCommand () {
    String s = "sampleCommand optionwithoutdash1 optionwithoutdash2 -f file1 -q -t option1 option2 -c option3";
    Command singleCommand = Command.parse (Arrays.asList (s.split ("\\s")), true);

    assertEquals ("sampleCommand", singleCommand.getMainCommand ());
    assertNotNull (singleCommand.getOptions ());

    assertArrayEquals (new String[] { "optionwithoutdash1", "optionwithoutdash2" },
                       singleCommand.getEmptyParameters ().toArray ());
    assertArrayEquals (new String[] { "file1" }, singleCommand.getArguments ("f").toArray ());
    assertArrayEquals (new String[0], singleCommand.getArguments ("q").toArray ());
    assertArrayEquals (new String[] { "option1", "option2" }, singleCommand.getArguments ("t").toArray ());
    assertArrayEquals (new String[] { "option3" }, singleCommand.getArguments ("c").toArray ());
  }
}