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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.wrapped.WrappedList;
import com.helger.commons.mime.MimeTypeParser;

import eu.toop.commons.dataexchange.v140.TDEErrorType;
import eu.toop.commons.dataexchange.v140.TDETOOPResponseType;
import eu.toop.commons.exchange.AsicWriteEntry;
import eu.toop.commons.exchange.ToopResponseWithAttachments140;
import eu.toop.commons.jaxb.ToopWriter;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.CodeType;

/**
 * This class is responsible for running a test scenario with respect to
 * its role. It does not finish until the test scenario has finished
 * or a timeout occurs
 */
public class TestScenarioManager {

  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioManager.class);


  /**
   * A map that holds context objects that make sure the Thread waits for the result of the step
   * before switching to the next step.
   */
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
   * @param testScenario the test scenario
   * @return the list
   */
  public static List<TestStepContext> runTest(TestScenario testScenario) {
    LOGGER.info("Run test for test scenario " + testScenario.getName() + " with role " + testScenario.getRole());

    switch (testScenario.getRole()) {
      case DC: {
        LOGGER.info("Test scenario running with role DC");
        //step 1, send a dc request.
        LOGGER.info("------------ STEP 1 ------------");
        TestStepContext testStepContext = executeStep1(testScenario);
        //step 4 receive a response
        if (testStepContext.isSuccess()) {
          LOGGER.info("------------ STEP 2 ------------");
          executeStep4(testScenario);
        }
      }
      break;

      case DP: {
        LOGGER.info("Test scenario running with role DP");
        //step 2 and 3, wait for a request and the response is sent back automatically for now
        LOGGER.info("------------ STEP 2 ------------");
        TestStepContext testStepContext = executeStep2(testScenario);

        if (testStepContext.isSuccess()) {
          LOGGER.info("------------ STEP 3 ------------");
          executeStep3(testScenario, (TestStepRequestContext) testStepContext);
        }
      }
      break;

      case BOTH: {
        LOGGER.info("Test scenario running with role BOTH");
        LOGGER.info("------------ STEP 1 ------------");
        TestStepContext testStepContext = executeStep1(testScenario);

        if (testStepContext.isSuccess()) {
          LOGGER.info("------------ STEP 2 ------------");
          testStepContext = executeStep2(testScenario);
        }

        if (testStepContext.isSuccess()) {
          LOGGER.info("------------ STEP 3 ------------");
          testStepContext = executeStep3(testScenario, (TestStepRequestContext) testStepContext);
        }

        if (testStepContext.isSuccess()) {
          LOGGER.info("------------ STEP 4 ------------");
          executeStep4(testScenario);
        }
      }
      break;
    }


    //TODO: check the array list testScenario.getExecutedTestSteps to compare
    //the executed steps, their success status with respect to the Role (DC, DP, BOTH)
    //you can do it also in the above switch statement
    return testScenario.getExecutedTestSteps();
  }

  /**
   * Execute first step (send request as DC)
   *
   * @param testScenario
   * @return
   */
  private static TestStepContext executeStep1(TestScenario testScenario) {
    TestStepContext testStepContext;
    try {
      ConnectorManager.sendDCRequest(testScenario.getRequestXMLReference());
      testStepContext = new TestStepSuccessContext(TestStep.TEST_STEP_SEND_REQUEST);
    } catch (Exception ex) {
      LOGGER.error("Failed to send dc request");
      LOGGER.error(ex.getMessage());
      testStepContext = new TestStepErrorContext(TestStep.TEST_STEP_SEND_REQUEST, ex.getMessage() == null ? "NONE" : ex.getMessage());
    }

    testScenario.addTestResult(testStepContext);
    return testStepContext;
  }

  /**
   * Execute second step (receive a request as DP)
   *
   * @param testScenario
   * @return
   */
  private static TestStepContext executeStep2(TestScenario testScenario) {
    //step 4, wait for a result in receive response.
    TestStepContext testStepContext = waitForTestStep(TestStep.TEST_STEP_RECEIVE_REQUEST);
    testScenario.addTestResult(testStepContext);
    return testStepContext;
  }

  /**
   * Execute third step (send a response as DP)
   *
   * @param testScenario
   * @param requestContext the test step and the request that was received in step 2
   * @return
   */
  private static TestStepContext executeStep3(TestScenario testScenario, TestStepRequestContext requestContext) {
    TestStepContext testStepContext;
    try {

      //if test scenario contains a response xml reference, use it as a response

      //otherwise create a response from the request and the response data/request-metadata.conf

      final TDETOOPResponseType aResponse;

      ArrayList<AsicWriteEntry> responseAttachments = new ArrayList<>();

      if (testScenario.getResponseXMLReference() != null)
        aResponse = ToopMessageCreator.createDPResponse(new FileInputStream(testScenario.getResponseXMLReference()));
      else if (testScenario.getResponseMetadataFileName() != null)
        aResponse = ToopMessageCreator.createDPResponse(requestContext.getToopRequestWithAttachments140().getRequest(), testScenario.getResponseMetadataFileName());
      else
        aResponse = ToopMessageCreator.createDPResponse(requestContext.getToopRequestWithAttachments140().getRequest(), "data/response-metadata.conf");

      ArrayList<String> tmp = testScenario.getResponseAttachments();
      if (tmp != null) {
        tmp.forEach(file -> {
          try (FileInputStream stream = new FileInputStream(file)) {
            //use the actual file name as the attachment name and extension as type
            String type = FilenameUtils.getExtension(file);
            //if no extension, then use binary extension
            if(type.isEmpty())
              type = "octet-stream";

            AsicWriteEntry asicWriteEntry = new AsicWriteEntry(FilenameUtils.getName(file),
                IOUtils.toByteArray(stream), MimeTypeParser.parseMimeType("application/" + type));
            responseAttachments.add(asicWriteEntry);
          } catch (IOException ioe) {
            LOGGER.error(ioe.getMessage(), ioe);
            throw new IllegalStateException("Couldn't parse attachment file " + file);
          }
        });
      }

      //FIXME: Fix the confusion between asic read entry with a response with attachments (the response to be written)
      ToopResponseWithAttachments140 toopResponseWithAttachments140 = new ToopResponseWithAttachments140(aResponse,
          new WrappedList<>(Collections.emptyList()));

      ConnectorManager.sendDPResponse(aResponse, responseAttachments);
      testStepContext = new TestStepResponseContext(TestStep.TEST_STEP_SEND_RESPONSE, toopResponseWithAttachments140);
    } catch (Exception ex) {
      LOGGER.error("Failed to send dc request");
      LOGGER.error(ex.getMessage(), ex);
      testStepContext = new TestStepErrorContext(TestStep.TEST_STEP_SEND_RESPONSE, ex.getMessage() == null ? "NONE" : ex.getMessage());
    }

    testScenario.addTestResult(testStepContext);
    return testStepContext;
  }

  /**
   * Execute the fourth step (receive a response as DC)
   *
   * @param testScenario
   * @return
   */
  private static TestStepContext executeStep4(TestScenario testScenario) {
    //step 4, wait for a result in receive response.
    TestStepContext testStepContext = waitForTestStep(TestStep.TEST_STEP_RECEIVE_RESPONSE);
    //check if we have a valid response
    if (testStepContext != null && testStepContext instanceof TestStepResponseContext) {
      ToopResponseWithAttachments140 toopResponseWithAttachments140 = ((TestStepResponseContext) testStepContext).getToopResponseWithAttachments140();

      TDETOOPResponseType aResponse = toopResponseWithAttachments140.getResponse();
      // Check all error codes
      if (aResponse.hasErrorEntries()) {
        for (TDEErrorType error : aResponse.getError()) {

          final CodeType errorCode = error.getErrorCode();

          // If the received error code doesn't exist in the test scenario's expected
          // list of error codes, then the test have failed.
          if (!testScenario.getExpectedErrorCodes().contains(errorCode.getValue())) {
            testStepContext = new TestStepErrorContext(TestStep.TEST_STEP_RECEIVE_RESPONSE,
                "Unexpected error code in the response: " + errorCode.getValue());
            //FIXME breaking the loop on the first unexpected code might be lossy
            // should all the unexpected codes be populated
            break;
          }
        }
      }

      // Log the response xml
      LOGGER.info(ToopWriter.response140().getAsString(aResponse));

      toopResponseWithAttachments140.attachments().forEach(asicReadEntry -> {
        //TODO: check the attachment against the config
        File dir = new File("data/tests/receivedattachments/" + testScenario.getParentConfig().getCategory() + "-" + testScenario.getName());
        dir.mkdirs();
        LOGGER.info("Attachment name: " + asicReadEntry.getEntryName());
        LOGGER.info("Attachment length: " + asicReadEntry.payload().length);
        try (FileOutputStream fos = new FileOutputStream(new File(dir, asicReadEntry.getEntryName()))) {
          fos.write(asicReadEntry.payload());
        } catch (IOException ex) {
        }
      });
    } else {
      testStepContext = new TestStepErrorContext(TestStep.TEST_STEP_RECEIVE_RESPONSE, "Couldn't receive response");
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
  private static @Nonnull
  TestStepContext waitForTestStep(TestStep testStep) {
    //find the associated test step context carrier to wait on
    TestStepContext[] testStepContextWaiter = testStepWaiterMap.get(testStep);
    synchronized (testStepContextWaiter) {
      testStepContextWaiter[0] = null; //clear it
      try {
        LOGGER.debug("Wait for test step " + testStep);
        testStepContextWaiter.wait(CommanderConfig.getTestStepWaitTimeout());

        if (testStepContextWaiter[0] == null)
          return new TestStepTimeoutContext(testStep);

        return testStepContextWaiter[0];
      } catch (InterruptedException e) {
        testStepContextWaiter[0] = null;
        LOGGER.error("Wait for test step " + testStep + " was interrupted");
        return new TestStepErrorContext(testStep, "Wait for test step " + testStep + " was interrupted");
      } finally {
        testStepContextWaiter[0] = null; //clear it
      }
    }
  }
}
