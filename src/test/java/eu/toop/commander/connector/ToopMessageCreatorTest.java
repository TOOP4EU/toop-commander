/**
 * Copyright (C) 2018 toop.eu
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
package eu.toop.commander.connector;

import org.junit.jupiter.api.Test;

import eu.toop.commander.ToopMessageCreator;
import eu.toop.commons.dataexchange.v120.TDETOOPRequestType;
import eu.toop.commons.dataexchange.v120.TDETOOPResponseType;

class ToopMessageCreatorTest {

  @Test
  void createDCRequest() throws Exception {
    TDETOOPRequestType dcRequest = ToopMessageCreator.createDCRequest("myid", "mycountry", "metadata.conf");
    byte[] bytes = ToopMessageCreator.serializeRequest(dcRequest);
    System.out.println(new String(bytes));
  }

  @Test
  void createDPResponse() throws Exception {
    TDETOOPResponseType dpResponse = ToopMessageCreator.createDPResponse("myid", "mycountry", "metadata.conf");
    byte[] bytes = ToopMessageCreator.serializeResponse(dpResponse);
    System.out.println(new String(bytes));
  }

}