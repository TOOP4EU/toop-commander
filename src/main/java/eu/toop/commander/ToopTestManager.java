/**
 * Copyright (C) 2018-2019 toop.eu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.commander;

import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commons.exchange.ToopRequestWithAttachments140;
import eu.toop.commons.exchange.ToopResponseWithAttachments140;
import eu.toop.iface.IToopInterfaceDC;
import eu.toop.iface.IToopInterfaceDP;
import eu.toop.iface.ToopInterfaceManager;

/**
 * The type Toop test manager.
 */
public class ToopTestManager {

  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopTestManager.class);

  /**
   * The singleton instance
   */
  private static ToopTestManager toopTestManager = new ToopTestManager();

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static ToopTestManager getInstance() {

    return toopTestManager;
  }

  /**
   * Private constructor for the singleton
   */
  private ToopTestManager() {

  }

  /**
   * Execute tests.
   *
   * @param testConfigFile the test config file
   * @param tests          the tests
   */
  public void executeTests(final String testConfigFile, final List<String> tests) {

    //backup the original dc-dp listener for the good old commands
    IToopInterfaceDC originalInterfaceDC = ToopInterfaceManager.getInterfaceDC();
    IToopInterfaceDP originalInterfaceDP = ToopInterfaceManager.getInterfaceDP();

    // Assign a listener to handle TOOP Requests and Responses
    final ToopTestManagerListener toopTestManagerListener = new ToopTestManagerListener();
    ToopInterfaceManager.setInterfaceDC(toopTestManagerListener);
    ToopInterfaceManager.setInterfaceDP(toopTestManagerListener);

    // Load all testScenarios
    final TestConfig testConfig = new TestConfig(testConfigFile);

    // This is the main loop for all the different test scenarios defined in
    // the test config file.
    for (TestScenario testScenario : testConfig.getTestScenarioList()) {

      // If the caller has specified specific test-cases to execute check if
      // this testScenario is present in the tests argument list.
      if (tests != null && !tests.contains(testScenario.getName())) {
        continue; // Test is not specified by the caller, goto next testScenario...
      }

      //run the test scenario, don't return until its finished!!!! (of course with a timeout)
      TestScenarioManager.runTest(testScenario);
    }

    // Log the test summary
    LOGGER.info(TestReporter.printReport(testConfig));

    // Export the test summary
    TestReporter.exportReport(testConfig, "samples/tests/reports/");

    // restore the original listeners
    ToopInterfaceManager.setInterfaceDC(originalInterfaceDC);
    ToopInterfaceManager.setInterfaceDP(originalInterfaceDP);
  }

  private class ToopTestManagerListener implements IToopInterfaceDC, IToopInterfaceDP {
    @Override
    public void onToopResponse(@Nonnull ToopResponseWithAttachments140 aResponse) {
      LOGGER.debug("Received a Toop Response");
      TestScenarioManager.fireTestStepOcurred(new TestStepContext(TestStep.TEST_STEP_RECEIVE_RESPONSE, aResponse.getResponse ()));
    }

    @Override
    public void onToopRequest(@Nonnull ToopRequestWithAttachments140 aRequest) {
      LOGGER.debug("Received a Toop Request");
      TestScenarioManager.fireTestStepOcurred(new TestStepContext(TestStep.TEST_STEP_RECEIVE_REQUEST, aRequest.getRequest ()));
    }

    @Override
    public void onToopErrorResponse(@Nonnull ToopResponseWithAttachments140 aResponse) {
      LOGGER.debug("Received a Toop Error Response");
      LOGGER.error(aResponse.toString());
    }
  }
}
