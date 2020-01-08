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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.ConfigValue;

/**
 * The type Test config.
 */
public class TestConfig {

  /**
   * The Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);
  private List<TestScenario> testScenarioList = new ArrayList<>();
  private String category;

  /**
   * Instantiates a new Test config.
   *
   * @param configFile the config file
   */
  public TestConfig(String configFile) {
    try {
      LOGGER.debug("Try to parse " + configFile);
      ConfigParseOptions opt = ConfigParseOptions.defaults();
      opt.setSyntax(ConfigSyntax.CONF);
      Config conf = ConfigFactory.parseFile(new File(configFile)).resolve();

      this.category = conf.getString("TestCategory");

      List<? extends ConfigObject> testConfig = conf.getObjectList("TestConfig");

      LOGGER.debug("There are " + testConfig.size() + " test scenarios in the configuration");
      for (ConfigObject co : testConfig) {
        String name = checkAndUnwrap(co, "TestName");
        String role = checkAndUnwrap(co, "Role");
        String requestXMLReference = checkAndUnwrap(co, "RequestXMLReference");

        String responseXMLReference = null;
        if(co.containsKey("ResponseXMLReference"))
          responseXMLReference = (String) co.get("ResponseXMLReference").unwrapped();

        String responseMetadataFile = null;
        if (co.containsKey("ResponseMetadataFile"))
          responseMetadataFile = checkAndUnwrap(co, "ResponseMetadataFile");

        @SuppressWarnings("unchecked")
        ArrayList<String> responseAttachments = null;
        if (co.containsKey("ResponseAttachments"))
          responseAttachments = (ArrayList<String>) co.get("ResponseAttachments").unwrapped();


        String reportTemplateReference = checkAndUnwrap(co, "ReportTemplateReference");
        String summary = checkAndUnwrap(co, "Summary");

        // Get expected error code list
        ConfigValue successCriteria = co.get("SuccessCriteria");
        Map<String, ConfigObject> errors = (Map<String, ConfigObject>) successCriteria.unwrapped();

        @SuppressWarnings("unchecked")
        ArrayList<String> expectedErrorCodeList = (ArrayList<String>) errors.get("ExpectedErrorCodes");
        // Create test scenario
        TestScenario testScenario = new TestScenario(
            this,
            name,
            TestScenario.Role.valueOf(role),
            requestXMLReference,
            responseXMLReference,
            responseMetadataFile,
            responseAttachments,
            reportTemplateReference,
            summary,
            expectedErrorCodeList);

        testScenarioList.add(testScenario);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  private String checkAndUnwrap(ConfigObject co, String testName) {
    ValueEnforcer.notNull(co, "config value " + testName + " cannot be null");
    return (String) co.get(testName).unwrapped();
  }

  /**
   * Gets test scenario list.
   *
   * @return the test scenario list
   */
  public List<TestScenario> getTestScenarioList() {
    return testScenarioList;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
