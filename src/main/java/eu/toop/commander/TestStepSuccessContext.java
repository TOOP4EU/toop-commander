package eu.toop.commander;

/**
 * Represents a test step context that is successful
 */
public class TestStepSuccessContext extends TestStepContext{
  /**
   * Instantiates a new Test step context.
   *
   * @param testStep the test step
   */
  public TestStepSuccessContext(TestStep testStep) {
    super(testStep, true);
  }
}
