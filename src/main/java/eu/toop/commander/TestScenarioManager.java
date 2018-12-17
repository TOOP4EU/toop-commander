package eu.toop.commander;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commons.dataexchange.TDEErrorType;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.commons.jaxb.ToopWriter;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.CodeType;

/**
 * This class is responsible for running a test scenario with respect to
 * its role. It does not finish until the test scenario has finished
 * or a timeout occurs
 */
public class TestScenarioManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioManager.class);

  private static final Map<TestStep, TestStepContext[]> testStepWaiterMap = new HashMap<>();


  static {
    testStepWaiterMap.put(TestStep.TEST_STEP_SEND_REQUEST, new TestStepContext[1]);
    testStepWaiterMap.put(TestStep.TEST_STEP_RECEIVE_REQUEST, new TestStepContext[1]);
    testStepWaiterMap.put(TestStep.TEST_STEP_SEND_RESPONSE, new TestStepContext[1]);
    testStepWaiterMap.put(TestStep.TEST_STEP_RECEIVE_RESPONSE, new TestStepContext[1]);
  }

  /**
   * Run the supplied test scenario with respect to its role and do not return
   * until it is finished or a certain timeout is expired
   *
   * @param testScenario
   */
  public static List<TestStepContext> runTest(TestScenario testScenario) {
    LOGGER.info("Run test for test scenario " + testScenario.getName() + " with role " + testScenario.getRole());

    switch (testScenario.getRole()) {
      case DC: {
        //step 1, send a dc request.
        TestStepContext testStepContext = executeStep1(testScenario);
        //step 4 receive a response
        if (testStepContext != null)
          executeStep4(testScenario);
      }
      break;

      case DP: {
        //step 2 and 3, wait for a request and the response is sent back automatically for now
        TestStepContext testStepContext = executeStep2(testScenario);

        if (testStepContext != null)
          executeStep3(testScenario, testStepContext);
      }
      break;

      case BOTH: {
        TestStepContext testStepContext = executeStep1(testScenario);

        if (testStepContext != null)
          testStepContext = executeStep2(testScenario);

        if (testStepContext != null)
          testStepContext = executeStep3(testScenario, testStepContext);

        if (testStepContext != null)
          executeStep4(testScenario);
      }
      break;
    }




    //TODO: check the array list testScenario.getExecutedTestSteps to compare
    //the executed steps, their success status with respect to the Role (DC, DP, BOTH)
    //you can do it also in the above switch statement
    return testScenario.getExecutedTestSteps ();
  }

  private static TestStepContext executeStep1(TestScenario testScenario) {
    TestStepContext testStepContext;
    try {
      ConnectorManager.sendDCRequest(testScenario.getRequestXMLReference());
      testStepContext = new TestStepContext(TestStep.TEST_STEP_SEND_REQUEST, true);
    } catch (Exception ex) {
      LOGGER.error("Failed to send dc request");
      LOGGER.error(ex.getMessage());
      testStepContext = new TestStepContext(TestStep.TEST_STEP_SEND_REQUEST, false, ex.getMessage());
    }

    testScenario.addTestResult(testStepContext);
    return testStepContext;
  }

  private static TestStepContext executeStep2(TestScenario testScenario) {
    //step 4, wait for a result in receive response.
    TestStepContext testStepContext = waitForTestStep(TestStep.TEST_STEP_RECEIVE_REQUEST);
    //check if we have a valid response
    if (testStepContext != null && testStepContext.getSatelliteObject() != null) {
      testStepContext.setSuccess(true);
    } else {
      testStepContext = new TestStepContext(testStepContext.getTestStep(), false, "Couldn't receive request");
    }

    testScenario.addTestResult(testStepContext);
    return testStepContext;
  }

  private static TestStepContext executeStep3(TestScenario testScenario, TestStepContext previousStepContext) {
    TestStepContext testStepContext;
    try {
      final TDETOOPResponseType aResponse = ToopMessageCreator.createDPResponse((TDETOOPRequestType) previousStepContext.getSatelliteObject(), "response-metadata.conf");
      ConnectorManager.sendDPResponse(aResponse);
      testStepContext = new TestStepContext(TestStep.TEST_STEP_SEND_RESPONSE, aResponse);
      testStepContext.setSuccess(true);
    } catch (Exception ex) {
      LOGGER.error("Failed to send dc request");
      LOGGER.error(ex.getMessage());
      testStepContext = new TestStepContext(TestStep.TEST_STEP_SEND_RESPONSE, false, ex.getMessage());
    }

    testScenario.addTestResult(testStepContext);
    return testStepContext;
  }

  private static TestStepContext executeStep4(TestScenario testScenario) {
    //step 4, wait for a result in receive response.
    TestStepContext testStepContext = waitForTestStep(TestStep.TEST_STEP_RECEIVE_RESPONSE);
    //check if we have a valid response
    if (testStepContext != null && testStepContext.getSatelliteObject() != null) {
      TDETOOPResponseType aResponse = (TDETOOPResponseType) testStepContext.getSatelliteObject();

      testStepContext.setSuccess(true);
      // Check all error codes
      if (aResponse.hasErrorEntries()) {
        for (TDEErrorType error : aResponse.getError()) {

          final CodeType errorCode = error.getErrorCode();

          // If the received error code doesn't exist in the test scenario's expected
          // list of error codes, then the test have failed.
          if (!testScenario.getExpectedErrorCodes().contains(errorCode.getValue())) {
            testStepContext.setSuccess(false);
            testStepContext.setResult("Unexpected error code in the response: " + errorCode.getValue());
          }
        }
      }

      // Log the response xml
      LOGGER.info(ToopWriter.response ().getAsString (aResponse));
    } else {
      testStepContext = new TestStepContext(TestStep.TEST_STEP_RECEIVE_RESPONSE, false, "Couldn't receive response");
    }

    testScenario.addTestResult(testStepContext);
    return testStepContext;
  }

  /**
   * When one of the test steps occur in the entire commander scope,
   * this method will be called with the necessary step type and a possible satellite
   * object attached to it as a <code>TestStepContext</code> object
   *
   * @param testStepContext The information related to the fired test step
   */
  public static void fireTestStepOcurred(TestStepContext testStepContext) {
    TestStepContext[] testStepContextWaiter = testStepWaiterMap.get(testStepContext.getTestStep());

    synchronized (testStepContextWaiter) {
      testStepContextWaiter[0] = testStepContext;
      testStepContextWaiter.notifyAll();
    }
  }


  /**
   * Wait on and consume the satellite object for a test step
   */
  private static TestStepContext waitForTestStep(TestStep testStep) {

    //find the associated test step context carrier to wait on
    TestStepContext[] testStepContextWaiter = testStepWaiterMap.get(testStep);
    synchronized (testStepContextWaiter) {
      testStepContextWaiter[0] = null; //clear it
      try {
        LOGGER.debug("Wait for test step " + testStep);
        testStepContextWaiter.wait(CommanderConfig.getTestStepWaitTimeout());
      } catch (InterruptedException e) {
        testStepContextWaiter[0] = null;
        LOGGER.error("Wait for test step " + testStep + " was interrupted");
        return new TestStepContext(testStep, "Wait for test step " + testStep + " was interrupted");
      }
    }

    //check if we have a value set
    synchronized (testStepContextWaiter) {
      try {
        return testStepContextWaiter[0];
      } finally {
        testStepContextWaiter[0] = null; //clear it
      }
    }
  }
}
