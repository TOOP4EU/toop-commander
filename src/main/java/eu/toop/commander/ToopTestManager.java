package eu.toop.commander;

import eu.toop.commander.async.ToopInterfaceListener;
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
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class ToopTestManager {

  private static ToopTestManager ourInstance = new ToopTestManager ();
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopTestManager.class);
  public static Semaphore lock = new Semaphore (1);
  public static TDETOOPResponseType aResponse = null;

  public static ToopTestManager getInstance () {

    return ourInstance;
  }

  private ToopTestManager () {

  }

  public void executeTests(final String testConfigFile) {

    // Assign a listener to handle TOOP Requests and Responses
    final ToopTestManagerListener toopTestManagerListener = new ToopTestManagerListener();
    ToopInterfaceManager.setInterfaceDC(toopTestManagerListener);
    ToopInterfaceManager.setInterfaceDP(toopTestManagerListener);

    // Load all testScenarios
    final TestConfig testConfig = new TestConfig (testConfigFile);
    for (int i=0; i<testConfig.getTestScenarioList ().size (); i++) {
      final TestScenario testScenario = testConfig.getTestScenarioList ().get (i);

      try {

        // Execute acquire the lock and initiate the test scenario
        lock.acquire ();

        LOGGER.info ("Running test " + (i+1) + "/" + testConfig.getTestScenarioList ().size () + ": " + testScenario.getName ());
        ConnectorManager.sendDCRequest(testScenario.getRequestXMLReference ());

        // Try to acquire the lock again
        // This will wait until the lock has been released by ToopTestManagerListener.onToopRequest
        lock.acquire ();

        // Compare the TOOP Response with the success criteria.
        boolean testHasFailed = false;

        // Check all error codes
        if (aResponse.hasErrorEntries ()) {
          for (TDEErrorType error : aResponse.getError ()) {

            final CodeType errorCode = error.getErrorCode ();

            // If the received error code doesn't exist in the test scenario's expected
            // list of error codes, then the test have failed.
            if (!testScenario.getExpectedErrorCodes ().contains (errorCode.getValue ())) {
              testHasFailed = true;
            }
          }
        }

        // Print the test result
        if (testHasFailed) {
          LOGGER.info ("Test failure!");
        } else {
          LOGGER.info ("Test success!");
        }

      } catch (InterruptedException e) {
        e.printStackTrace ();
      } finally {
        lock.release ();
      }
    }

    // Unassign the listener
    ToopInterfaceManager.setInterfaceDC(null);
    ToopInterfaceManager.setInterfaceDP(null);
  }

  public class ToopTestManagerListener implements IToopInterfaceDC, IToopInterfaceDP {

    @Override
    public void onToopResponse(@Nonnull TDETOOPResponseType aResponse) throws IOException {
      ToopTestManager.aResponse = aResponse;
      ToopTestManager.lock.release ();
    }

    @Override
    public void onToopRequest(@Nonnull TDETOOPRequestType aRequest) throws IOException {
      LOGGER.debug("Received a Toop Request");

      final TDETOOPResponseType aResponse = ToopMessageCreator.createDPResponse (aRequest, "response-metadata.conf");

      ConnectorManager.sendDPResponse (aResponse);
    }
  }
}
