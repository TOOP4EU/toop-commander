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
package eu.toop.commander.async;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commander.ConnectorManager;
import eu.toop.commander.ToopMessageCreator;
import eu.toop.commons.dataexchange.TDETOOPErrorMessageType;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import eu.toop.iface.IToopInterfaceDC;
import eu.toop.iface.IToopInterfaceDP;

public class ToopInterfaceListener implements IToopInterfaceDC, IToopInterfaceDP {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopInterfaceListener.class);

  @Override
  public void onToopResponse(@Nonnull TDETOOPResponseType aResponse) throws IOException {
    LOGGER.debug("Received a Toop Response");
    LOGGER.debug(aResponse.toString());
  }

  @Override
  public void onToopErrorMessage(@Nonnull TDETOOPErrorMessageType aErrorMessage) throws IOException {
    LOGGER.debug("onToopErrorMessage");
    LOGGER.debug(aErrorMessage.toString());
  }

  @Override
  public void onToopRequest(@Nonnull TDETOOPRequestType aRequest) throws IOException {
    LOGGER.debug("Received a Toop Request");

    final TDETOOPResponseType aResponse = ToopMessageCreator.createDPResponse (aRequest, "response-metadata.conf");

    ConnectorManager.sendDPResponse (aResponse);
  }
}
