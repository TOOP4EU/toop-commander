/**
 * Copyright (C) 2018-2020 toop.eu
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
package eu.toop.commander.cli;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.jline.builtins.Completers;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 * A class that helps autocomplete and suggestions for the command line interface
 */
public class ToopCommanderCli {
  public static final String CMD_HELP = "help";
  public static final String CMD_SEARCH_DP_BY_COUNTRY = "search-dp-by-country";
  public static final String CMD_SEARCH_DP_BY_DPTYPE = "search-dp-by-dptype";
  public static final String CMD_SEND_DC_REQUEST = "send-dc-request";
  public static final String CMD_SEND_DP_RESPONSE = "send-dp-response";
  public static final String CMD_RUN_TEST = "run-test";
  public static final String CMD_QUIT = "quit";
  public static final String CMD_CAT = "cat";
  /**
   * Read lines from the console, with input editing.
   */
  private final LineReader reader;
  /**
   * Formats date for displaying the prompt
   */
  private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd HH:MM:ss");


  private String prompt;

  public ToopCommanderCli(){
    this("toop-commander> ",
        ".tchistory",
        Arrays.asList(CMD_HELP, CMD_SEARCH_DP_BY_COUNTRY, CMD_SEARCH_DP_BY_DPTYPE,
            CMD_SEND_DC_REQUEST, CMD_SEND_DP_RESPONSE, CMD_RUN_TEST, CMD_QUIT, CMD_CAT));
  }
  /**
   * Default constructor
   */
  public ToopCommanderCli(String prompt, String historyFile, List<String> stringList) {
    this.prompt = prompt;

    TerminalBuilder builder = TerminalBuilder.builder();

    Completer second = new SecondCompleter();

    ArgumentCompleter completer = new ArgumentCompleter(
        new StringsCompleter(stringList),
        second
    );

    completer.setStrict(false);

    Parser parser = null;

    Terminal terminal;
    try {
      terminal = builder.build();
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }

    DefaultHistory defaultHistory = new DefaultHistory();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        defaultHistory.save();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }));

    reader = LineReaderBuilder.builder()
        .terminal(terminal).variable(LineReader.HISTORY_FILE, new File(historyFile))
        .completer(completer)
        .parser(parser).history(defaultHistory)
        .build();

  }

  /**
   * Read one line from the command line
   *
   * @return boolean
   */
  public boolean readLine() {
    boolean b = reader.readLine(prompt, sdf.format(Calendar.getInstance().getTime()), (MaskingCallback) null, null) != null;
    return b;
  }

  /**
   * get the list of the words (tokens) parsed from the cli
   *
   * @return words
   */
  public List<String> getWords() {
    return reader.getParsedLine().words();
  }


  /**
   * Second level auto completer helper (running after the primary command)
   */
  private static class SecondCompleter implements Completer {
    /**
     * The Send completer.
     */
    SendCompleter sendCompleter = new SendCompleter();
    /**
     * The File name completer.
     */
    Completers.FileNameCompleter fileNameCompleter = new Completers.FileNameCompleter();

    /**
     * The search-dp-by-country query completer.
     */
    DPQueryByCountryCompleter dPQueryByCountryCompleter = new DPQueryByCountryCompleter();


    /**
     * The search-dp-by-dptype query completer.
     */
    DPQueryByDPTypeCompleter dpTypeCompleter = new DPQueryByDPTypeCompleter();


    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
      //check the command
      String command = line.words().get(0);
      switch (command) {
        case CMD_CAT:
          fileNameCompleter.complete(reader, line, candidates);
          break;

        case CMD_SEARCH_DP_BY_COUNTRY:
          dPQueryByCountryCompleter.complete(reader, line, candidates);
          break;

        case CMD_SEARCH_DP_BY_DPTYPE:
          dpTypeCompleter.complete(reader, line, candidates);
          break;

        case CMD_HELP:
        case CMD_QUIT:
          break;

        case CMD_SEND_DC_REQUEST:
        case CMD_SEND_DP_RESPONSE:
          sendCompleter.complete(reader, line, candidates);
          break;
      }
    }
  }

  /**
   * Command line completer for ID Query
   */
  private static class DPQueryByCountryCompleter implements Completer {
    private final Candidate display_raw_result;
    private final Candidate connectivity_test;
    private final Candidate country_code;
    private final Candidate doc_type_id;

    private final List<Candidate> countryCandidates;
    private final List<Candidate> docTypeCandidates;
    private final List<Candidate> mainOptions;

    /**
     * Default constructor
     */
    DPQueryByCountryCompleter() {
      mainOptions = new ArrayList<>();
      display_raw_result = new Candidate("-raw", "-raw", "", "Display Raw Result", null, null, false);
      connectivity_test = new Candidate("-t", "-t", "", "Connectivity test", null, null, false);
      country_code = new Candidate("-c", "-c", "", "Country Code", null, null, false);
      doc_type_id = new Candidate("-d", "-d", "", "Doc Type id", null, null, false);
      mainOptions.add(country_code);
      mainOptions.add(connectivity_test);
      mainOptions.add(display_raw_result);
      mainOptions.add(doc_type_id);

      countryCandidates = new ArrayList<>();
      countryCandidates.add(new Candidate("AX", "AX", "", "Aland Islands", null, null, false));
      countryCandidates.add(new Candidate("AT", "AT", "", "Australia", null, null, false));
      countryCandidates.add(new Candidate("DK", "DK", "", "Denmark", null, null, false));
      countryCandidates.add(new Candidate("GF", "GF", "", "French Guiana", null, null, false));
      countryCandidates.add(new Candidate("GR", "GR", "", "Greece", null, null, false));
      countryCandidates.add(new Candidate("IT", "IT", "", "Italy", null, null, false));
      countryCandidates.add(new Candidate("PF", "PF", "", "French Polynesia", null, null, false));
      countryCandidates.add(new Candidate("SE", "SE", "", "Sweden", null, null, false));
      countryCandidates.add(new Candidate("SK", "SK", "", "Slovakia", null, null, false));
      countryCandidates.add(new Candidate("SV", "SV", "", "El Salvador", null, null, false));

      docTypeCandidates = new ArrayList<>();

      docTypeCandidates.add(new Candidate("toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-1p40::Response##urn:eu.toop.response.registeredorganization::1.40", "1p40-Response-regis.org.1.40", "", "Registered Org. Response 1.40", null, null, false));
      docTypeCandidates.add(new Candidate("toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-1p10::Response##urn:eu.toop.response.registeredorganization::1.10", "1p10-Response-regis.org.1.10", "", "Registered Org. Response 1.10", null, null, false));
      docTypeCandidates.add(new Candidate("toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-1p40::Request##urn:eu.toop.request.registeredorganization::1.40", "1p10-Request-regis.org.1.10", "", "Registered Org. Request 1.10", null, null, false));

   }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
      if (line.wordIndex() == 0) {
        return;
      }

      if (line.wordIndex() == 1) {
        candidates.addAll(mainOptions);
      } else {
        String prev = line.words().get(line.wordIndex() - 1);

        if (prev.equals("-d")) {
          candidates.addAll(docTypeCandidates);
        } else if (prev.equals("-c")) {
          candidates.addAll(countryCandidates);
        } else {
          candidates.addAll(mainOptions);
        }
      }

    }


  }


  /**
   * Command line completer for ID Query
   */
  private static class DPQueryByDPTypeCompleter implements Completer {

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
      if (line.wordIndex() == 0) {
        return;
      }

      if (line.wordIndex() == 1) {
        candidates.add(new Candidate("-d"));
      }
    }

  }

  private static class SendCompleter implements Completer {
    private List<Candidate> sendNewMessageCandidates = new ArrayList<>();
    /**
     * The File name completer.
     */
    Completers.FileNameCompleter fileNameCompleter = new Completers.FileNameCompleter();

    /**
     * Instantiates a new Send completer.
     */
    public SendCompleter() {
      sendNewMessageCandidates.add(new Candidate("-i", "-i", "", "Data Subject Identifier", null, null, false));
      sendNewMessageCandidates.add(new Candidate("-c", "-c", "", "Data Subject Country", null, null, false));
      sendNewMessageCandidates.add(new Candidate("-m", "-m", "", "metadata file", null, null, false));
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
      if (line.wordIndex() == 0) {
        return;
      }

      if (line.wordIndex() == 1) {
        candidates.add(new Candidate("-new", "-new", "", "create new document", null, null, false));
        candidates.add(new Candidate("-f", "-f", "", "send existing file", null, null, false));
      } else if (line.wordIndex() == 2) {
        String arg = line.words().get(1);

        switch (arg) {
          case "-f":
            fileNameCompleter.complete(reader, line, candidates);
            break;
          case "-new":
            candidates.addAll(sendNewMessageCandidates);
            break;
        }
      } else {

        if (line.words().get(1).equals("-f")) {
          fileNameCompleter.complete(reader, line, candidates);
          return;
        }

        if (line.wordIndex() % 2 == 0 && line.words().get(1).equals("-new")) {
          candidates.addAll(sendNewMessageCandidates);
        }
      }

    }
  }
}
