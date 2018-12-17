package eu.toop.commander;

import java.util.ArrayList;
import java.util.List;

public class TestReport {

  private final List<TestScenario> successfulTests = new ArrayList<> ();
  private final List<TestScenario> failedTests = new ArrayList<> ();
  private final List<TestScenario> executedTests = new ArrayList<> ();
  private final List<TestScenario> skippedTests = new ArrayList<> ();

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

  public List<TestScenario> getSuccessfulTests () {

    return successfulTests;
  }

  public List<TestScenario> getFailedTests () {

    return failedTests;
  }

  public List<TestScenario> getSkippedTests () {

    return skippedTests;
  }

  public List<TestScenario> getExecutedTests () {

    return executedTests;
  }
}