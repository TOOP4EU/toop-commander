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

  public List<TestStepContext> getExecutedTestSteps() {
    return executedTestSteps;
  }
}
