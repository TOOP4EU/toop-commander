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

import java.util.ArrayList;
import java.util.List;

/**
 * The type Test report.
 */
public class TestReport {

  private final List<TestScenario> successfulTests = new ArrayList<> ();
  private final List<TestScenario> failedTests = new ArrayList<> ();
  private final List<TestScenario> executedTests = new ArrayList<> ();
  private final List<TestScenario> skippedTests = new ArrayList<> ();

  /**
   * Instantiates a new Test report.
   *
   * @param testConfig the test config
   */
  public TestReport(TestConfig testConfig) {

    for (int i = 0; i < testConfig.getTestScenarioList().size(); i++) {
      final TestScenario testScenario = testConfig.getTestScenarioList().get(i);

      if (testScenario.getExecutedTestSteps ().isEmpty ()) {
        skippedTests.add (testScenario);
        continue;
      }

      boolean testSuccess = true;
      for (TestStepContext testStepContext : testScenario.getExecutedTestSteps ()) {
        if (!testStepContext.isSuccess()) {
          testSuccess = false;
        }
      }

      if (testSuccess) {
        successfulTests.add (testScenario);
      } else {
        failedTests.add (testScenario);
      }

      executedTests.add (testScenario);
    }
  }

  /**
   * Gets successful tests.
   *
   * @return the successful tests
   */
  public List<TestScenario> getSuccessfulTests () {

    return successfulTests;
  }

  /**
   * Gets failed tests.
   *
   * @return the failed tests
   */
  public List<TestScenario> getFailedTests () {

    return failedTests;
  }

  /**
   * Gets skipped tests.
   *
   * @return the skipped tests
   */
  public List<TestScenario> getSkippedTests () {

    return skippedTests;
  }

  /**
   * Gets executed tests.
   *
   * @return the executed tests
   */
  public List<TestScenario> getExecutedTests () {

    return executedTests;
  }
}
