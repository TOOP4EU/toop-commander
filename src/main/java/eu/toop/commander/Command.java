package eu.toop.commander;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.helger.commons.ValueEnforcer;

/**
 * This class represents a mainCommand and its options parsed as a map
 */
public class Command {
  /**
   * The main command, the first word in the list
   */
  private String mainCommand;

  /**
   * The map that associates an option with its parameters
   */
  private Map<String, List<String>> options;

  /**
   * Private constructor
   */
  private Command() {

  }

  /**
   * @return the actual command
   */
  public String getMainCommand() {
    return mainCommand;
  }

  /**
   * Get the entire parameter list that is associated with the given option
   * @param key the option
   * @return the parameter list or null (if the map is empty or key doesn't hit)
   */
  public List<String> getArguments(String key) {
    if (options == null || options.size() == 0)
      return null;

    return options.get(key);
  }

  /**
   * Traverse the whole list and group with respect to options that start with a dash.<br>
   * For example, the following words <br>
   * <code>sampleCommand optionwithoutdash1 optionwithoutdash2 -f file1 -q -t option1 option2 -c option3</code><br>
   * is parsed as a linked hash map as follows <br>
   * <pre>
   *   mainCommand: sampleCommand,
   *   option map:
   *      "" -&gt; (optionwithoutdash1, optionwithoutdash2)
   *      f -&gt; (file1)
   *      q -&gt; ()
   *      t -&gt; (option1, option2)
   *      c -&gt; (option3)
   * </pre>
   *
   * @param words word list
   * @return A new command
   */
  public static Command parse(List<String> words) {
    ValueEnforcer.notEmpty(words, "The word list cannot be null or empty");

    Command command = new Command();
    command.mainCommand = words.get(0);

    if (words.size() > 1) {

      Map<String, List<String>> options = new LinkedHashMap<>();

      int listSize = words.size();
      String currentKey = ""; //empty key
      ArrayList<String> currentList = new ArrayList<>();
      options.put(currentKey, currentList);
      for (int i = 1; i < listSize; ++i) {
        String current = words.get(i);
        if (current.startsWith("-")) {
          currentKey = current.substring(1); //skip dash
          currentList = new ArrayList<>();
          options.put(currentKey, currentList);
        } else {
          currentList.add(current); //populate the current option
        }
      }
      command.options = options;
    }
    return command;
  }

  /**
   * @return the entire options map
   */
  public Map<String, List<String>> getOptions() {
    return options;
  }

  /**
   * @return the parameters that don't have a leading option
   */
  public List<String> getEmptyParameters() {
    return getArguments("");
  }

  /**
   * @param key key to check
   * @return true if the options map contains the given <code>key</code>
   */
  public boolean hasOption(String key) {
    if(options == null || options.isEmpty())
      return false;

    return options.containsKey(key);
  }
}
