package eu.toop.commander;

import java.util.ArrayList;
import java.util.List;

public class TestScenario {

  public enum Role {
    DC, DP, BOTH
  }
  private final String name;
  private final Role role;
  private final String requestXMLReference;
  private final String reportTemplateReference;
  private final String summary;
  private final List<String> expectedErrorCodes;

  private final List<TestStepContext> executedTestSteps = new ArrayList<>();

  public TestScenario(final String name,
                      final Role role,
                      final String requestXMLReference,
                      final String reportTemplateReference,
                      final String summary,
                      final List<String> expectedErrorCodes) {

    this.name = name;
    this.role = role;
    this.requestXMLReference = requestXMLReference;
    this.reportTemplateReference = reportTemplateReference;
    this.summary = summary;
    this.expectedErrorCodes = expectedErrorCodes;
  }

  public String getName () {
    return name;
  }

  public Role getRole () {
    return role;
  }

  public String getRequestXMLReference () {
    return requestXMLReference;
  }

  public String getReportTemplateReference () {
    return reportTemplateReference;
  }

  public String getSummary () {
    return summary;
  }

  public List<String> getExpectedErrorCodes () {
    return expectedErrorCodes;
  }

  public void addTestResult(TestStepContext testStepContext) {
    executedTestSteps.add(testStepContext);
  }

}
