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

import javax.annotation.Nullable;

/**
 * The type Test scenario.
 */
public class TestScenario {

  /**
   * The enum Role.
   */
  public enum Role {
    /**
     * Dc role.
     */
    DC,
    /**
     * Dp role.
     */
    DP,
    /**
     * Both role.
     */
    BOTH
  }

  private final String name;
  private final Role role;
  private final String requestXMLReference;
  private final String responseXMLReference;
  private final String responseMetadataFileName;
  private final ArrayList<String> responseAttachments;
  private final String reportTemplateReference;
  private final String summary;
  private final List<String> expectedErrorCodes;

  private final List<TestStepContext> executedTestSteps = new ArrayList<>();

  /**
   * Instantiates a new Test scenario.
   * @param parentConfig parent configuration
   *  @param name                    the name
   * @param role                    the role
   * @param requestXMLReference     the request xml reference
   * @param responseXMLReference     the response xml reference (for DP and BOTH Roles)
   * @param responseMetadataFileName  the file that contains information about the reponse to be created
   *                                  for this scenario (can be null). <p><b>Both <code>responseXMLReference</code> and <code>responseMetadataFileName</code></b> can be provided.
   *                                  However the <code>responseXMLReference</code>
   *                                  has a precedence over <code>responseMetadataFileName</code>.
   *                                  i.e. if <code>responseXMLReference</code> is not null, it will be used and
   *                                  <code>responseMetadataFileName</code> will be discarded.
   *                                  If both values are null, then we assume the file <code>data/response-metadata.conf</code> as
   *                                  the default data source for the possible response to be created.
   *                                  </p>
   * @param responseAttachments  the list of attachemnts that should be added to the asic file
   *                             created for this test scenario
   * @param reportTemplateReference the report template reference
   * @param summary                 the summary
   * @param expectedErrorCodes      the expected error codes
   */
  public TestScenario(TestConfig parentConfig,
                      final String name,
                      final Role role,
                      final String requestXMLReference,
                      @Nullable  final String responseXMLReference,
                      @Nullable final String responseMetadataFileName,
                      @Nullable ArrayList<String> responseAttachments,
                      final String reportTemplateReference,
                      final String summary,
                      @Nullable final List<String> expectedErrorCodes) {

    this.name = name;
    this.role = role;
    this.requestXMLReference = requestXMLReference;
    this.responseXMLReference = responseXMLReference;
    this.responseMetadataFileName = responseMetadataFileName;
    this.responseAttachments = responseAttachments;
    this.reportTemplateReference = reportTemplateReference;
    this.summary = summary;
    this.expectedErrorCodes = expectedErrorCodes;
    this.parentConfig = parentConfig;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets role.
   *
   * @return the role
   */
  public Role getRole() {
    return role;
  }

  /**
   * Gets request xml reference.
   *
   * @return the request xml reference
   */
  public String getRequestXMLReference() {
    return requestXMLReference;
  }



  public String getResponseMetadataFileName() {
    return responseMetadataFileName;
  }


  /**
   * @return the response xml reference
   */
  public String getResponseXMLReference() {
    return responseXMLReference;
  }

  public ArrayList<String> getResponseAttachments() {
    return responseAttachments;
  }

  /**
   * Gets report template reference.
   *
   * @return the report template reference
   */
  public String getReportTemplateReference() {
    return reportTemplateReference;
  }

  /**
   * Gets summary.
   *
   * @return the summary
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Gets expected error codes.
   *
   * @return the expected error codes
   */
  public List<String> getExpectedErrorCodes() {
    return expectedErrorCodes;
  }

  /**
   * Add test result.
   *
   * @param testStepContext the test step context
   */
  public void addTestResult(TestStepContext testStepContext) {
    executedTestSteps.add(testStepContext);
  }

  /**
   * Gets executed test steps.
   *
   * @return the executed test steps
   */
  public List<TestStepContext> getExecutedTestSteps() {
    return executedTestSteps;
  }


  private TestConfig parentConfig;

  public TestConfig getParentConfig() {
    return parentConfig;
  }

  public void setParentConfig(TestConfig parentConfig) {
    this.parentConfig = parentConfig;
  }

}
