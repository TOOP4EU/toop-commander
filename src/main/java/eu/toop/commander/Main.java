package eu.toop.commander;

import eu.toop.commander.async.ToopInterfaceListener;
import eu.toop.commander.connector.ConnectorManager;
import eu.toop.commander.servlets.RootServlet;
import eu.toop.iface.ToopInterfaceManager;
import eu.toop.iface.servlet.ToDCServlet;
import eu.toop.iface.servlet.ToDPServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.jline.builtins.Completers;
import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.jline.builtins.Completers.TreeCompleter.node;

/**
 * @author yerlibilgin
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    int port = CommanderConfig.getHttpPort();

    if (args.length >= 1) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException ex) {
        LOGGER.error("Invalid port  " + args[0]);
        throw new IllegalStateException("Invalid port  " + args[0]);
      }
    }
    LOGGER.info("Starting toop-commander on port " + port);
    Server server = new Server(port);

    ServletHandler servletHandler = new ServletHandler();
    server.setHandler(servletHandler);

    LOGGER.info("Registering root servlet");
    servletHandler.addServletWithMapping(RootServlet.class, "/");
    LOGGER.info("Registering the to-dc endpoint on " + CommanderConfig.getToDcEndpoint());
    servletHandler.addServletWithMapping(ToDCServlet.class, CommanderConfig.getToDcEndpoint());
    LOGGER.info("Registering the to-dp endpoint on " + CommanderConfig.getToDpEndpoint());
    servletHandler.addServletWithMapping(ToDPServlet.class, CommanderConfig.getToDpEndpoint());


    //register endpoint listeners

    ToopInterfaceListener interfaceListener = new ToopInterfaceListener();
    ToopInterfaceManager.setInterfaceDC(interfaceListener);

    LOGGER.info("Starting server");
    server.start();

    LOGGER.info("Entering CLI mode");

    TerminalBuilder builder = TerminalBuilder.builder();


    Completers.FileNameCompleter fileCompleter = new Completers.FileNameCompleter();

    Completer completer = new ArgumentCompleter(
        new StringsCompleter("help", "send-dc-request", "send-dp-response", "quit", "cat"),
        (reader, line, candidates) -> {
          if (line.wordIndex() != 1)
            return;

          String command = line.words().get(0);
          if (command.equals("send-dc-request") || command.equals("send-dp-response") || command.equals("cat"))
            fileCompleter.complete(reader, line, candidates);
        });

    Parser parser = null;

    Terminal terminal = builder.build();

    LineReader reader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(completer)
        .parser(parser)
        .build();


    System.out.println();
    String prompt = "toop-commander> ";
    String line;


    try {
      while ((line = reader.readLine(prompt, "", (MaskingCallback) null, null)) != null) {
        line = line.trim();

        try {
          LOGGER.debug("input command " + line);

          List<String> commands = parseCommand(line);

          if (commands != null && commands.size() > 0) {
            switch (commands.get(0)) {
              case "help":
                printHelpMessage();
                break;

              case "cat": {
                if (commands.size() != 2)
                  throw new IllegalArgumentException("Invalid command");

                String file = commands.get(1);
                printFile(file);
              }
              break;

              case "send-dc-request": {
                if (commands.size() != 2)
                  throw new IllegalArgumentException("Invalid command");

                String file = commands.get(1);

                ConnectorManager.sendDCRequest(file);
              }
              break;

              case "send-dp-response": {
                if (commands.size() != 2)
                  throw new IllegalArgumentException("Invalid command");

                String file = commands.get(1);

                ConnectorManager.sendDPResponse(file);
              }

              break;

              case "quit":
                quit(server);
                break;

              default:
                printHelpMessage();
                break;
            }
          } else {
            printHelpMessage();
          }
        } catch (Exception ex) {
          LOGGER.error(ex.getClass() + ": " + ex.getMessage());
          printHelpMessage();
        } finally {
          Thread.sleep(100);
        }
      }
    } catch (UserInterruptException ex) {
      quit(server);
    }

  }

  /**
   * <code>cat</code> the file
   *
   * @param file
   */
  private static void printFile(String file) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

      String line;

      while ((line = br.readLine()) != null) {
        System.out.println(line); //don't use logger for this. no need
      }
    } catch (Exception ex) {
      LOGGER.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
    }
  }

  private static void quit(Server server) throws Exception {
    LOGGER.info("Stopping the server");
    server.stop();
    server.join();
    System.exit(0);
  }


  private static void printHelpMessage() {
    LOGGER.debug("Commands:");
    LOGGER.debug("  help                     print help message");
    LOGGER.debug("  cat                      print contents of a file");
    LOGGER.debug("  send-dc-request   file   Send the request  file to the configured connectors /from-dc endpoint");
    LOGGER.debug("  send-dp-response  file   Send the response file to the configured connectors /from-dp endpoint");
    LOGGER.debug("  quit                     exit toop-commander");
  }

  /**
   * Tokenize the input string respecting the " character
   *
   * @param line
   * @return
   */
  private static List<String> parseCommand(String line) {

    char[] array = line.toCharArray();

    char[] quoteArray = new char[line.length()];
    char[] freeArray = new char[line.length()];
    int indexInQuote = 0;
    int freeIndex = 0;

    List<String> commandline = new ArrayList();

    boolean quoteReqion = false;

    for (int i = 0; i < array.length; ++i) {
      char currentChar = array[i];
      switch (currentChar) {
        case '"':
          quoteReqion = !quoteReqion;
          if (!quoteReqion) {
            //finish current quote
            commandline.add(new String(quoteArray, 0, indexInQuote));
            indexInQuote = 0;
          }
          break;

        case '\t':
        case ' ':
          //still in quote region?
          if (quoteReqion) {
            quoteArray[indexInQuote++] = currentChar;
            break;
          }

          //skip whitespaces
          do {
            i++;
          } while (i < array.length && Character.isWhitespace(array[i]));

          if (i != array.length)
            i--;

          //finish current word
          commandline.add(new String(freeArray, 0, freeIndex));
          freeIndex = 0;

          break;

        default:
          if (quoteReqion) {
            quoteArray[indexInQuote++] = currentChar;
          } else {
            freeArray[freeIndex++] = currentChar;
          }
          break;
      }
    }

    if (quoteReqion) {
      throw new IllegalStateException("Invalid command.");
    }

    if (freeIndex != 0) {
      commandline.add(new String(freeArray, 0, freeIndex));
    }
    return commandline;
  }
}
