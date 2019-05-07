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
package eu.toop.commander.async;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commander.ConnectorManager;
import eu.toop.commander.ToopMessageCreator;
import eu.toop.commons.dataexchange.v140.TDETOOPResponseType;
import eu.toop.commons.exchange.ToopRequestWithAttachments140;
import eu.toop.commons.exchange.ToopResponseWithAttachments140;
import eu.toop.commons.jaxb.ToopWriter;
import eu.toop.iface.IToopInterfaceDC;
import eu.toop.iface.IToopInterfaceDP;

/**
 * The listener that processes the toop request, responses and errors
 */
public class ToopInterfaceListener implements IToopInterfaceDC, IToopInterfaceDP {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopInterfaceListener.class);

  @Override
  public void onToopResponse(@Nonnull ToopResponseWithAttachments140 aResponseWA) {
    LOGGER.debug("Received a Toop Response");

    // Log the response xml
    LOGGER.info(ToopWriter.response140().getAsString(aResponseWA.getResponse ()));
  }

  @Override
  public void onToopRequest(@Nonnull ToopRequestWithAttachments140 aRequestWA) {
    LOGGER.debug("Received a Toop Request");

    final TDETOOPResponseType aResponse = ToopMessageCreator.createDPResponse(aRequestWA.getRequest (), "response-metadata.conf");

    ConnectorManager.sendDPResponse(aResponse);
  }

  @Override
  public void onToopErrorResponse(@Nonnull ToopResponseWithAttachments140 aResponseWA) {
    LOGGER.debug("Received a Toop Error Response");

    // Log the response xml
    LOGGER.info(ToopWriter.response140().getAsString(aResponseWA.getResponse ()));
  }
}
