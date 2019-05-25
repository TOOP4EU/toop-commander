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

import eu.toop.commons.dataexchange.v140.TDETOOPRequestType;
import eu.toop.commons.exchange.ToopRequestWithAttachments140;

/**
 * A class that represents contextual information related
 * to a test step where the result of the action is a request received from the other side
 */
public class TestStepRequestContext extends TestStepSuccessContext{

  /**
   * The toop request received from the other side for this test step
   */
  private final ToopRequestWithAttachments140 toopRequestWithAttachments140;

  /**
   * Instantiates a new Test step context.
   *
   * @param testStep        the test step
   * @param toopRequestWithAttachments140  the associated TOOP Request
   */
  public TestStepRequestContext(TestStep testStep, ToopRequestWithAttachments140 toopRequestWithAttachments140) {
    super(testStep);
    this.toopRequestWithAttachments140 = toopRequestWithAttachments140;
  }

  public ToopRequestWithAttachments140 getToopRequestWithAttachments140() {
    return toopRequestWithAttachments140;
  }
}