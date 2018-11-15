package eu.toop.commander;

import eu.toop.commons.dataexchange.TDEErrorType;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.iface.IToopInterfaceDC;
import eu.toop.iface.IToopInterfaceDP;
import eu.toop.iface.ToopInterfaceManager;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.CodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class ToopTestManager {

  private static ToopTestManager ourInstance = new ToopTestManager();
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopTestManager.class);

  public static ToopTestManager getInstance() {

    return ourInstance;
  }

  private ToopTestManager() {

  }

  public void executeTests(final String testConfigFile) {

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
    for (int i = 0; i < testConfig.getTestScenarioList().size(); i++) {
      //start the test
      final TestScenario testScenario = testConfig.getTestScenarioList().get(i);

      LOGGER.info("Running test " + (i + 1) + "/" + testConfig.getTestScenarioList().size() + ": " + testScenario.getName());

      //run the test scenario, don't return until its finished!!!! (of course with a timeout)
      TestScenarioManager.runTest(testScenario);
    }

    // restore the original listeners
    ToopInterfaceManager.setInterfaceDC(originalInterfaceDC);
    ToopInterfaceManager.setInterfaceDP(originalInterfaceDP);
  }

  private class ToopTestManagerListener implements IToopInterfaceDC, IToopInterfaceDP {
    @Override
    public void onToopResponse(@Nonnull TDETOOPResponseType aResponse) {
      LOGGER.debug("Received a Toop Response");
      LOGGER.debug(aResponse.toString());

      TestScenarioManager.fireTestStepOcurred(new TestStepContext(TestStep.TEST_STEP_RECEIVE_RESPONSE, aResponse));
    }

    @Override
    public void onToopRequest(@Nonnull TDETOOPRequestType aRequest) {
      LOGGER.debug("Received a Toop Request");

      TestScenarioManager.fireTestStepOcurred(new TestStepContext(TestStep.TEST_STEP_RECEIVE_REQUEST, aRequest));
    }
  }
}
