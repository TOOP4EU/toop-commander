package eu.toop.commander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class TestConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);
  private List<TestScenario> testScenarioList = new ArrayList<>();

  public TestConfig(String configFile) {

    try {

      File xmlFile = new File(configFile);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(xmlFile);

      doc.getDocumentElement().normalize();

      XPath xPath = XPathFactory.newInstance().newXPath();
      NodeList testNodeList = (NodeList) xPath.compile("/TestConfig/Test").evaluate(doc, XPathConstants.NODESET);

      // Parse each Test
      for (int i = 0; i < testNodeList.getLength(); i++) {
        Node testNode = testNodeList.item(i);

        String name = (String) xPath.evaluate("@name", testNode, XPathConstants.STRING);
        String role = (String) xPath.evaluate("Role", testNode, XPathConstants.STRING);
        String requestXMLReference = (String) xPath.evaluate("RequestXMLReference", testNode, XPathConstants.STRING);
        String reportTemplateReference = (String) xPath.evaluate("ReportTemplateReference", testNode, XPathConstants.STRING);
        String summary = (String) xPath.evaluate("Summary", testNode, XPathConstants.STRING);

        // Parse expected error code list
        NodeList errorCodeNodeList = (NodeList) xPath.evaluate("SuccessCriteria/ExpectedErrorCodes/ErrorCode", testNode, XPathConstants.NODESET);
        List<String> expectedErrorCodeList = new ArrayList<>();
        for (int j = 0; j < errorCodeNodeList.getLength(); j++) {
          expectedErrorCodeList.add(errorCodeNodeList.item(j).getTextContent());
        }

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
      e.printStackTrace();
    }
  }

  public List<TestScenario> getTestScenarioList() {
    return testScenarioList;
  }
}
