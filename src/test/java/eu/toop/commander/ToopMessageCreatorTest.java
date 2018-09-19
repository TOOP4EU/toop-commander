package eu.toop.commander;

import com.typesafe.config.Config;
import eu.toop.commons.dataexchange.TDEDataElementRequestType;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Anton Wiklund
 */
public class ToopMessageCreatorTest {

  private static final Logger s_aLogger = LoggerFactory.getLogger (ToopMessageCreatorTest.class);

  @Test
  void createDCRequestTest() {

    final String metadataFile = "metadata.conf";

    final TDETOOPRequestType aRequest = ToopMessageCreator.createDCRequest (null, null, metadataFile);

    Config conf = ToopMessageCreator.parseMetadataFile(metadataFile);

    assertNotNull (aRequest.getDataRequestSubject ());
    assertNotNull (aRequest.getDataRequestSubject ().getDataRequestSubjectTypeCode());
    assertNotNull (aRequest.getDataRequestSubject ().getDataRequestSubjectTypeCode().getValue ());

    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getPersonIdentifier());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getFirstName());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getFamilyName());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getBirthDate());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress().getStreetName ());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress().getStreetNumber ());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress().getCity ());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress().getPostCode ());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress().getCountry ());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress().getCountryCode ());

    assertNotNull (aRequest.getDataRequestSubject ().getLegalEntity());
    assertNotNull (aRequest.getDataRequestSubject ().getLegalEntity().getLegalEntityIdentifier ());
    assertNotNull (aRequest.getDataRequestSubject ().getLegalEntity().getLegalName ());
    assertNotNull (aRequest.getDataRequestSubject ().getLegalEntity().getLegalEntityLegalAddress ());

    assertNotNull (aRequest.getDataElementRequest ());

    assertEquals (aRequest.getDataRequestSubject ().getDataRequestSubjectTypeCode ().getValue (), conf.getString("ToopMessage.dataSubjectTypeCode"));

    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getPersonIdentifier ().getValue (), conf.getString("ToopMessage.NaturalPerson.identifier"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getFirstName ().getValue (), conf.getString("ToopMessage.NaturalPerson.firstName"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getFamilyName ().getValue (), conf.getString("ToopMessage.NaturalPerson.familyName"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getNaturalPersonLegalAddress ().getStreetName ().getValue (), conf.getString("ToopMessage.NaturalPerson.Address.streetName"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getNaturalPersonLegalAddress ().getStreetNumber ().getValue (), conf.getString("ToopMessage.NaturalPerson.Address.streetNumber"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getNaturalPersonLegalAddress ().getCity ().getValue (), conf.getString("ToopMessage.NaturalPerson.Address.city"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getNaturalPersonLegalAddress ().getPostCode ().getValue (), conf.getString("ToopMessage.NaturalPerson.Address.postCode"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getNaturalPersonLegalAddress ().getCountry ().getValue (), conf.getString("ToopMessage.NaturalPerson.Address.country"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getNaturalPersonLegalAddress ().getCountryCode ().getValue (), conf.getString("ToopMessage.NaturalPerson.Address.countryCode"));
    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getNaturalPersonLegalAddress ().getAddressLineCount (), conf.getList ("ToopMessage.NaturalPerson.Address.addressLines").size());
    assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(aRequest.getDataRequestSubject ()
        .getNaturalPerson ().getBirthDate ().toGregorianCalendar().getTime()),
        conf.getString("ToopMessage.NaturalPerson.birthDate"));

    assertEquals(aRequest.getDataRequestSubject ().getLegalEntity ().getLegalEntityIdentifier ().getValue (), conf.getString("ToopMessage.LegalPerson.identifier"));
    assertEquals(aRequest.getDataRequestSubject ().getLegalEntity ().getLegalName ().getValue (), conf.getString("ToopMessage.LegalPerson.name"));
    assertEquals(aRequest.getDataRequestSubject ().getLegalEntity ().getLegalEntityLegalAddress ().getAddressLineCount (), conf.getList ("ToopMessage.LegalPerson.Address.addressLines").size());

    assertEquals(aRequest.getDataElementRequest ().size (), conf.getList ("ToopMessage.Concepts.conceptList").size());
    for (int i=0; i<aRequest.getDataElementRequest ().size(); i++) {

      TDEDataElementRequestType requestType = aRequest.getDataElementRequest ().get(i);

      assertNotNull (requestType.getConceptRequest ());
      assertNotNull (requestType.getConceptRequest ().getConceptNamespace ());
      assertNotNull (requestType.getConceptRequest ().getConceptName ());

      assertEquals(requestType.getConceptRequest ().getConceptNamespace ().getValue (), conf.getString ("ToopMessage.Concepts.conceptNamespace"));
      assertEquals(requestType.getConceptRequest ().getConceptName ().getValue (), conf.getStringList ("ToopMessage.Concepts.conceptList").get (i));
    }

    // Unable to test these properties listed in the metadata.conf
    s_aLogger.warn ("Can't test ToopMessage.NaturalPerson.birthPlace because it doesn't exist in class TDENaturalPersonType");
    s_aLogger.warn ("Can't test ToopMessage.NaturalPerson.nationality because it doesn't exist in class TDENaturalPersonType");
    s_aLogger.warn ("Can't test ToopMessage.LegalPerson.nationality because it doesn't exist in class TDELegalEntityType");
  }

  @Test
  void createDCRequestTestOverride() {

    final String metadataFile = "metadata.conf";

    final String identifier = "XX/YY/123456789";
    final String country = "ZZ";

    final TDETOOPRequestType aRequest = ToopMessageCreator.createDCRequest (identifier, country, metadataFile);

    assertNotNull (aRequest.getDataRequestSubject ());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getPersonIdentifier());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getFirstName());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress());
    assertNotNull (aRequest.getDataRequestSubject ().getNaturalPerson().getNaturalPersonLegalAddress().getCountryCode());

    assertEquals(aRequest.getDataRequestSubject ().getNaturalPerson ().getPersonIdentifier ().getValue (), identifier);

    // Unable to test these properties listed in the metadata.conf
    s_aLogger.warn ("Can't test ToopMessage.NaturalPerson.nationality because it doesn't exist in class TDENaturalPersonType");
  }

  @Test
  void createDCResponse() {

    final String metadataFile = "metadata.conf";
    final String responseMetadataFile = "response-metadata.conf";


    final TDETOOPRequestType aRequest = ToopMessageCreator.createDCRequest (null, null, metadataFile);
    final TDETOOPResponseType aResponse = ToopMessageCreator.createDPResponse (aRequest, responseMetadataFile);

    Config conf = ToopMessageCreator.parseMetadataFile(responseMetadataFile);

    assertNotNull (aResponse);
    assertNotNull (aResponse.getDataProvider ());
    assertNotNull (aResponse.getDataProvider ().getDPIdentifier ());
    assertNotNull (aResponse.getDataProvider ().getDPName ());
    assertNotNull (aResponse.getDataProvider ().getDPElectronicAddressIdentifier ());
    assertNotNull (aResponse.getDataProvider ().getDPLegalAddress ());
    assertNotNull (aResponse.getDataProvider ().getDPLegalAddress ().getCountryCode ());

    assertEquals (aResponse.getDataProvider ().getDPIdentifier ().getSchemeID (), conf.getString("ToopMessage.DataProvider.schemeId"));
    assertEquals (aResponse.getDataProvider ().getDPIdentifier ().getValue (), conf.getString("ToopMessage.DataProvider.identifier"));
    assertEquals (aResponse.getDataProvider ().getDPName ().getValue (), conf.getString("ToopMessage.DataProvider.name"));
    assertEquals (aResponse.getDataProvider ().getDPElectronicAddressIdentifier ().getValue (), conf.getString("ToopMessage.DataProvider.electronicAddressIdentifier"));
    assertEquals (aResponse.getDataProvider ().getDPLegalAddress ().getCountryCode ().getValue (), conf.getString("ToopMessage.DataProvider.countryCode"));
  }
}
