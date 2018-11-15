package eu.toop.commander;

public enum TestStep {
  /**
   * Step 1 - sening the request
   */
  TEST_STEP_SEND_REQUEST(1),
  /**
   * Step 2 - receiving a request
   */
  TEST_STEP_RECEIVE_REQUEST(2),

  /**
   * Step 3 - sending a response
   */
  TEST_STEP_SEND_RESPONSE(3),

  /**
   * Step 4 - receiving a response
   */
  TEST_STEP_RECEIVE_RESPONSE(4);

  public final int stepCode;

  TestStep(int stepCode){
    this.stepCode = stepCode;
  }
}
