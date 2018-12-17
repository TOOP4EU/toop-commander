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

public class TestConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);
  private List<TestScenario> testScenarioList = new ArrayList<>();

  public TestConfig(String configFile) {
    try {
      LOGGER.debug("Try to parse " + configFile);
      ConfigParseOptions opt = ConfigParseOptions.defaults();
      opt.setSyntax(ConfigSyntax.CONF);
      Config conf = ConfigFactory.parseFile(new File(configFile)).resolve();

      List<? extends ConfigObject> testConfig = conf.getObjectList("TestConfig");

      LOGGER.debug("There are " + testConfig.size() + " test scenarios in the configuration");
      for (ConfigObject co : testConfig) {
        String name = checkAndUnwrap(co, "TestName");
        String role = checkAndUnwrap(co, "Role");
        String requestXMLReference = checkAndUnwrap(co, "RequestXMLReference");
        String reportTemplateReference = checkAndUnwrap(co, "ReportTemplateReference");
        String summary = checkAndUnwrap(co, "Summary");

        // Get expected error code list
        ConfigValue successCriteria = co.get("SuccessCriteria");
        Map<String, ConfigObject> errors = (Map<String, ConfigObject>) successCriteria.unwrapped();

        ArrayList<String> expectedErrorCodeList = (ArrayList<String>) errors.get("ExpectedErrorCodes");
        // Create test scenario
        TestScenario testScenario = new TestScenario(name,
            TestScenario.Role.valueOf(role),
            requestXMLReference,
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

  public List<TestScenario> getTestScenarioList() {
    return testScenarioList;
  }
}
