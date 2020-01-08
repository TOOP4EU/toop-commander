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
package eu.toop.commander;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Test reporter.
 */
public class TestReporter {
  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TestReporter.class);

  /**
   * Export report.
   *
   * @param testConfig   the test config
   * @param reportFolder the report folder
   */
  public static void exportReport (TestConfig testConfig, String reportFolder) {

    TestReport report = new TestReport (testConfig);

    StringBuilder sb = new StringBuilder ();
    final DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

    String head =
        "<head>\n" +
        "    <style>\n" +
        "        td {\n" +
        "            border: 1px solid black;\n" +
        "        }\n" +
        "    </style>\n" +
        "</head>";
    sb.append (head + "\n");

    for (TestScenario testScenario : report.getExecutedTests ()) {

      try {
        File htmlTemplateFile = new File(testScenario.getReportTemplateReference ());
        String htmlString = FileUtils.readFileToString(htmlTemplateFile, StandardCharsets.UTF_8);

        htmlString = htmlString.replace("$(NAME)", testScenario.getName ());
        htmlString = htmlString.replace("$(ROLE)", testScenario.getRole ().name ());
        htmlString = htmlString.replace("$(DATE)", sdf.format(new Date()));
        htmlString = htmlString.replace("$(SUMMARY)", testScenario.getSummary ());

        String[] status = new String[4];
        String[] results = new String[4];
        for (int i=0; i<TestStep.values ().length; i++) {
          status[i] = "N/A";
          results[i] = "N/A";
        }
        for (TestStepContext testStep : testScenario.getExecutedTestSteps ()) {

          if (testStep.isSuccess ()) {
            status[testStep.getTestStep ().stepCode - 1] = "Success";
          } else {
            status[testStep.getTestStep ().stepCode - 1] = "Failed";
          }

          if (testStep instanceof TestStepErrorContext) {
            results[testStep.getTestStep ().stepCode - 1] = ((TestStepErrorContext) testStep).getErrorMessage();
          }
        }
        for (int i=0; i<TestStep.values ().length; i++) {
          TestStep testStep = TestStep.values()[i];
          htmlString = htmlString.replace(String.format ("$(%s_STATUS)", testStep.name ()), status[i]);
          htmlString = htmlString.replace(String.format ("$(%s_RESULT)", testStep.name ()), results[i]);
        }

        sb.append (htmlString + "<br><br>\n");

      } catch (IOException e) {
        e.printStackTrace ();
      }
    }

    String reportName = String.format ("Test report - %s-%s.html", testConfig.getCategory(), sdf.format(new Date()));

    try {
      File newHtmlFile = new File(reportFolder + reportName);
      FileUtils.writeStringToFile(newHtmlFile, sb.toString (), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace ();
    }
  }

  /**
   * Print report string.
   *
   * @param testConfig the test config
   * @return the string
   */
  public static String printReport (TestConfig testConfig) {

    TestReport report = new TestReport (testConfig);

    StringBuilder testSummary = new StringBuilder();

    testSummary.append ("\n\n\n");
    testSummary.append ("Final test summary:\n");
    testSummary.append (String.format ("  Number of tests: %d\n", testConfig.getTestScenarioList ().size ()));
    testSummary.append (String.format ("  Number of successful tests: %d\n", report.getSuccessfulTests ().size ()));
    testSummary.append (String.format ("  Number of failed tests: %d\n", report.getFailedTests ().size ()));
    testSummary.append (String.format ("  Number of skipped tests: %d\n", report.getSkippedTests ().size ()));

    if (report.getFailedTests ().size () > 0) {
      testSummary.append ("\n");
      testSummary.append ("Failed tests:\n");

      for (TestScenario failedTestScenario : report.getFailedTests ()) {

        testSummary.append (String.format("  Test [%s]: \n", failedTestScenario.getName ()));
        for (TestStepContext testStepContext : failedTestScenario.getExecutedTestSteps ()) {

          if (testStepContext instanceof TestStepErrorContext) {

            testSummary.append (String.format ("    Failure in step [%d]: \"%s\"\n",
                testStepContext.getTestStep ().stepCode, ((TestStepErrorContext) testStepContext).getErrorMessage()));
          }
        }
      }
    }

    if (report.getSkippedTests ().size () > 0) {

      testSummary.append ("\n");
      testSummary.append ("Skipped tests:\n");
      for (TestScenario skippedTestScenario : report.getSkippedTests ()) {

        testSummary.append (String.format ("  Test [%s]\n", skippedTestScenario.getName ()));
      }
    }

    return testSummary.toString ();
  }
}
