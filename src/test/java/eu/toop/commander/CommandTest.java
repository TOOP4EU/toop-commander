package eu.toop.commander;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CommandTest {
  @Test(expected = IllegalArgumentException.class)
  public void parseEmptyCommandList() throws Exception {
    Command.parse(new ArrayList<>());
  }

  @Test
  public void parseOneCommand(){
    Command singleCommand = Command.parse(Arrays.asList("singleCommand"));

    assertEquals("singleCommand", singleCommand.getMainCommand());
    assertNull(singleCommand.getOptions());
  }

  @Test
  public void parseComplexCommand(){
    String s = "sampleCommand optionwithoutdash1 optionwithoutdash2 -f file1 -q -t option1 option2 -c option3";
    Command singleCommand = Command.parse(Arrays.asList(s.split("\\s")));

    assertEquals("sampleCommand", singleCommand.getMainCommand());
    assertNotNull(singleCommand.getOptions());

    assertArrayEquals(new String[]{"optionwithoutdash1", "optionwithoutdash2"}, singleCommand.getEmptyParameters().toArray());
    assertArrayEquals(new String[]{"file1"}, singleCommand.getArguments("f").toArray());
    assertArrayEquals(new String[0], singleCommand.getArguments("q").toArray());
    assertArrayEquals(new String[]{"option1", "option2"}, singleCommand.getArguments("t").toArray());
    assertArrayEquals(new String[]{"option3"}, singleCommand.getArguments("c").toArray());
  }
}