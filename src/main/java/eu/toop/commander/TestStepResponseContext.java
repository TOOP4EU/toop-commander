/**
 * Copyright (C) 2018-2020 toop.eu
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

import eu.toop.commons.exchange.ToopResponseWithAttachments140;

/**
 * A class that represents contextual information related
 * to a test step where a TOOP Response is received from the other side
 */
public class TestStepResponseContext extends TestStepSuccessContext {

  /**
   * The TOOP Response object associated with this test step
   */
  private final ToopResponseWithAttachments140 toopResponseWithAttachments140;

  /**
   * Instantiates a new Test step context.
   *  @param testStep        the test step
   * @param toopResponseWithAttachments140 the response object
   */
  public TestStepResponseContext(TestStep testStep, ToopResponseWithAttachments140 toopResponseWithAttachments140) {
    super(testStep);
    this.toopResponseWithAttachments140 = toopResponseWithAttachments140;
  }

  public ToopResponseWithAttachments140 getToopResponseWithAttachments140() {
    return toopResponseWithAttachments140;
  }
}
