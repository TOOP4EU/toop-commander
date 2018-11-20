package eu.toop.commander;

import javax.annotation.Nullable;

/**
 * A class that represents contextual information related
 * to a test step
 */
public class TestStepContext {


  private TestStep testStep;

  private boolean success;

  private String result;

  @Nullable
  private Object satelliteObject;


  public TestStepContext() {
  }

  public TestStepContext(TestStep testStep, boolean success) {
    this(testStep, success, "N/A");
  }

  public TestStepContext(TestStep testStep, boolean success, String result) {
    this.testStep = testStep;
    this.success = success;
    this.result = result;
  }


  public TestStepContext(TestStep testStep, Object satelliteObject) {
    this.testStep = testStep;
    this.satelliteObject = satelliteObject;
    success = true;
  }


  public TestStep getTestStep() {
    return testStep;
  }

  public Object getSatelliteObject() {
    return satelliteObject;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getResult() {
    return result;
  }

  public void setSuccess(boolean b) {
    this.success = b;
  }
}
