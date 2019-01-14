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
package eu.toop.commander.cli;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class ToopCommanderCli {

  LineReader reader;
  private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd HH:MM:ss");

  public ToopCommanderCli() {
    TerminalBuilder builder = TerminalBuilder.builder();

    Completer second = new SecondCompleter();

    ArgumentCompleter completer = new ArgumentCompleter(
        new StringsCompleter("help", "send-dc-request", "send-dp-response", "run-test", "quit", "cat"),
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
        .terminal(terminal).variable(LineReader.HISTORY_FILE, new File(".tchistory"))
        .completer(completer)
        .parser(parser).history(defaultHistory)
        .build();

  }

  public boolean readLine() {
    boolean b = reader.readLine("toop-commander> ", sdf.format(Calendar.getInstance().getTime()), (MaskingCallback) null, null) != null;
    return b;
  }

  public List<String> getWords() {
    return reader.getParsedLine().words();
  }


  private static class SecondCompleter implements Completer {
    SendCompleter sendCompleter = new SendCompleter();
    Completers.FileNameCompleter fileNameCompleter = new Completers.FileNameCompleter();

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
      //check the command
      String command = line.words().get(0);
      switch (command) {
        case "cat":
          fileNameCompleter.complete(reader, line, candidates);
          break;

        case "help":
        case "quit":
          break;

        case "send-dc-request":
        case "send-dp-response":
          sendCompleter.complete(reader, line, candidates);
          break;
      }
    }
  }

  private static class SendCompleter implements Completer {
    private List<Candidate> sendNewMessageCandidates = new ArrayList<>();
    Completers.FileNameCompleter fileNameCompleter = new Completers.FileNameCompleter();

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
