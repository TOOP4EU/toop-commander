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
 * The enum Test step.
 */
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

  /**
   * The Step code.
   */
  public final int stepCode;

  TestStep(int stepCode){
    this.stepCode = stepCode;
  }
}
