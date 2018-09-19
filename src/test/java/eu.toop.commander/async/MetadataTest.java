package eu.toop.commander.async;

import com.typesafe.config.Config;
import eu.toop.commander.connector.ToopMessageCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Anton Wiklund
 */
public class MetadataTest {

  @Test
  void metadataTest() {

    final String metadataFile = "metadata.conf";

    Config conf = ToopMessageCreator.parseMetadataFile(metadataFile);

    assertNotNull(conf.getObject("ToopMessage"));
    assertNotNull(conf.getString("ToopMessage.dataSubjectTypeCode"));

    assertNotNull(conf.getObject("ToopMessage.NaturalPerson"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.identifier"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.firstName"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.familyName"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.birthPlace"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.birthDate"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.nationality"));

    assertNotNull(conf.getObject ("ToopMessage.NaturalPerson.Address"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.Address.streetName"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.Address.streetNumber"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.Address.city"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.Address.postCode"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.Address.country"));
    assertNotNull(conf.getString("ToopMessage.NaturalPerson.Address.countryCode"));
    assertNotNull(conf.getList ("ToopMessage.NaturalPerson.Address.addressLines"));

    assertNotNull(conf.getObject("ToopMessage.LegalPerson"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.identifier"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.name"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.nationality"));

    assertNotNull(conf.getObject ("ToopMessage.LegalPerson.Address"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.Address.streetName"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.Address.streetNumber"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.Address.city"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.Address.postCode"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.Address.country"));
    assertNotNull(conf.getString("ToopMessage.LegalPerson.Address.countryCode"));
    assertNotNull(conf.getList ("ToopMessage.LegalPerson.Address.addressLines"));

    assertNotNull(conf.getObject ("ToopMessage.Concepts"));
    assertNotNull(conf.getString ("ToopMessage.Concepts.conceptNamespace"));
    assertNotNull(conf.getList ("ToopMessage.Concepts.conceptList"));
  }

  @Test
  void responseMetadataTest() {

    final String metadataFile = "response-metadata.conf";

    Config conf = ToopMessageCreator.parseMetadataFile(metadataFile);

    assertNotNull(conf.getObject("ToopMessage"));
    assertNotNull(conf.getObject("ToopMessage.DataProvider"));
    assertNotNull(conf.getString("ToopMessage.DataProvider.schemeId"));
    assertNotNull(conf.getString("ToopMessage.DataProvider.identifier"));
    assertNotNull(conf.getString("ToopMessage.DataProvider.name"));
    assertNotNull(conf.getString("ToopMessage.DataProvider.electronicAddressIdentifier"));
    assertNotNull(conf.getString("ToopMessage.DataProvider.countryCode"));
  }
}
