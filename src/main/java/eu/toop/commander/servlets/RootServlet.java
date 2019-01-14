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
package eu.toop.commander.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.toop.commander.CommanderConfig;

/**
 * @author yerlibilgin
 */
public class RootServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(RootServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getOutputStream().print("<html><head><title>Toop Commander</title></head><body><h2>Toop Commander is UP and Running</h2>" +
        "<h3>Endpoints</h3><br/>" +
        "<ul>" +
        "<li> DC: <a href='" + CommanderConfig.getToDcEndpoint() + "'>" + CommanderConfig.getToDcEndpoint() +  "</a></li>" +
        "<li> DP: <a href='" + CommanderConfig.getToDpEndpoint() + "'>" + CommanderConfig.getToDpEndpoint() +  "</a></li>" +
        "</body></html>");
    resp.getOutputStream().flush();
  }
}