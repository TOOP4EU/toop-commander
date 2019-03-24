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

import javax.annotation.Nullable;

/**
 * A class that represents contextual information related
 * to a test step
 */
public class TestStepContext {

  /**
   * The actual test step wrapped in this context
   */
  private TestStep testStep;

  /**
   * Was the test step successful?
   */
  private boolean success;

  /**
   * String result of the test step
   */
  private String result;

  /**
   * A context free object that is carried together with the test step
   */
  @Nullable
  private Object satelliteObject;


  /**
   * Instantiates a new Test step context.
   */
  public TestStepContext() {
  }

  /**
   * Instantiates a new Test step context.
   *
   * @param testStep the test step
   * @param success  the success
   */
  public TestStepContext(TestStep testStep, boolean success) {
    this(testStep, success, "N/A");
  }

  /**
   * Instantiates a new Test step context.
   *
   * @param testStep the test step
   * @param success  the success
   * @param result   the result
   */
  public TestStepContext(TestStep testStep, boolean success, String result) {
    this.testStep = testStep;
    this.success = success;
    this.result = result;
  }


  /**
   * Instantiates a new Test step context.
   *
   * @param testStep        the test step
   * @param satelliteObject the satellite object
   */
  public TestStepContext(TestStep testStep, Object satelliteObject) {
    this.testStep = testStep;
    this.satelliteObject = satelliteObject;
    success = true;
  }


  /**
   * Gets test step.
   *
   * @return the test step
   */
  public TestStep getTestStep() {
    return testStep;
  }

  /**
   * Gets satellite object.
   *
   * @return the satellite object
   */
  public Object getSatelliteObject() {
    return satelliteObject;
  }

  /**
   * Is success boolean.
   *
   * @return the boolean
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets result.
   *
   * @param result the result
   */
  public void setResult(String result) {
    this.result = result;
  }

  /**
   * Gets result.
   *
   * @return the result
   */
  public String getResult() {
    return result;
  }

  /**
   * Sets success.
   *
   * @param b the b
   */
  public void setSuccess(boolean b) {
    this.success = b;
  }
}
