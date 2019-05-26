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

/**
 * A class that represents contextual information related
 * to a test step
 */
public class TestStepContext {

  private final boolean success;
  /**
   * The actual test step wrapped in this context
   */
  private final TestStep testStep;

  /**
   * Instantiates a new Test step context.
   *
   * @param testStep        the test step
   * @param success success or failure?
   */
  public TestStepContext(TestStep testStep, boolean success) {
    this.testStep = testStep;
    this.success = success;
  }


  /**
   * Gets test step.
   *
   * @return the test step
   */
  public TestStep getTestStep() {
    return testStep;
  }

  public boolean isSuccess() {
    return success;
  }
}
