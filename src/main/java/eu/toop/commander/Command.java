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
   * Get the actual command
   * @return
   */
  public String getMainCommand() {
    return mainCommand;
  }

  /**
   * Get the entire parmeter list that is associated with the given option
   * @param key the option
   * @return the parameter list or null (if the map is empty or key doesn't hit)
   */
  public List<String> getArguments(String key) {
    if (options == null || options.size() == 0)
      return null;

    if (options.containsKey(key)){
      return options.get(key);
    }

    return null;
  }

  /**
   * Traverse the whole list and group with respect to options that start with a dash.<br/>
   * For example, the following words <br/>
   * <code>sampleCommand optionwithoutdash1 optionwithoutdash2 -f file1 -q -t option1 option2 -c option3</code><br/>
   * is parsed as a linked hash map as follows <br/>
   * <pre>
   *   mainCommand: sampleCommand,
   *   option map:
   *      "" -> (optionwithoutdash1, optionwithoutdash2)
   *      f -> (file1)
   *      q -> ()
   *      t -> (option1, option2)
   *      c -> (option3)
   * </pre>
   *
   * @param words
   * @return
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
   * Return the entire options map
   * @return
   */
  public Map<String, List<String>> getOptions() {
    return options;
  }

  /**
   * Returns the parameters that don't have a leading option
   * @return
   */
  public List<String> getEmptyParameters() {
    return getArguments("");
  }

  /**
   * Returns true if the options map contains the given <code>key</code>
   * @param key
   * @return
   */
  public boolean hasOption(String key) {
    if(options == null || options.isEmpty())
      return false;

    return options.containsKey(key);
  }
}
