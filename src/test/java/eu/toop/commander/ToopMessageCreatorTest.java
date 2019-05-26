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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

import eu.toop.commons.dataexchange.v140.TDEDataElementRequestType;
import eu.toop.commons.dataexchange.v140.TDEDataProviderType;
import eu.toop.commons.dataexchange.v140.TDETOOPRequestType;
import eu.toop.commons.dataexchange.v140.TDETOOPResponseType;

/**
 * @author Anton Wiklund
 */
public class ToopMessageCreatorTest {

  private static final Logger s_aLogger = LoggerFactory.getLogger (ToopMessageCreatorTest.class);

  @Test
  void createDCRequestTest() {

    final String metadataFile = "data/request-metadata.conf";

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
        .getNaturalPerson ().getBirthDate ().getValue ().toGregorianCalendar().getTime()),
        conf.getString("ToopMessage.NaturalPerson.birthDate"));

    assertEquals(aRequest.getDataElementRequest ().size (), conf.getList ("ToopMessage.Concepts.conceptList").size());
    for (int i=0; i<aRequest.getDataElementRequest ().size(); i++) {

      TDEDataElementRequestType requestType = aRequest.getDataElementRequest ().get(i);

      assertNotNull (requestType.getConceptRequest ());
      assertNotNull (requestType.getConceptRequest ().getConceptNamespace ());
      assertNotNull (requestType.getConceptRequest ().getConceptName ());

      assertEquals(requestType.getConceptRequest ().getConceptNamespace ().getValue (), conf.getString ("ToopMessage.Concepts.conceptNamespace"));
      assertEquals(requestType.getConceptRequest ().getConceptName ().getValue (), conf.getStringList ("ToopMessage.Concepts.conceptList").get (i));
    }

    // Unable to test these properties listed in the request-metadata.conf
    s_aLogger.warn ("Can't test ToopMessage.NaturalPerson.birthPlace because it doesn't exist in class TDENaturalPersonType");
    s_aLogger.warn ("Can't test ToopMessage.NaturalPerson.nationality because it doesn't exist in class TDENaturalPersonType");
    s_aLogger.warn ("Can't test ToopMessage.LegalPerson.nationality because it doesn't exist in class TDELegalEntityType");
  }

  @Test
  void createDCRequestTestOverride() {

    final String metadataFile = "data/request-metadata.conf";

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

    final String metadataFile = "data/request-metadata.conf";
    final String responseMetadataFile = "data/response-metadata.conf";


    final TDETOOPRequestType aRequest = ToopMessageCreator.createDCRequest (null, null, metadataFile);
    final TDETOOPResponseType aResponse = ToopMessageCreator.createDPResponse (aRequest, responseMetadataFile);

    Config conf = ToopMessageCreator.parseMetadataFile(responseMetadataFile);

    assertNotNull (aResponse);
    assertNotNull (aResponse.getDataProvider ());
    assertEquals (1, aResponse.getDataProvider ().size ());
    TDEDataProviderType aDP = aResponse.getDataProviderAtIndex (0);
    assertNotNull (aDP);
    assertNotNull (aDP.getDPIdentifier ());
    assertNotNull (aDP.getDPName ());
    assertNotNull (aResponse.getRoutingInformation ().getDataProviderElectronicAddressIdentifier ());
    assertNotNull (aDP.getDPLegalAddress ());
    assertNotNull (aDP.getDPLegalAddress ().getCountryCode ());

    assertEquals (aDP.getDPIdentifier ().getSchemeID (), conf.getString("ToopMessage.DataProvider.schemeId"));
    assertEquals (aDP.getDPIdentifier ().getValue (), conf.getString("ToopMessage.DataProvider.identifier"));
    assertEquals (aDP.getDPName ().getValue (), conf.getString("ToopMessage.DataProvider.name"));
    assertEquals (aResponse.getRoutingInformation ().getDataProviderElectronicAddressIdentifier ().getValue (), conf.getString("ToopMessage.DataProvider.electronicAddressIdentifier"));
    assertEquals (aDP.getDPLegalAddress ().getCountryCode ().getValue (), conf.getString("ToopMessage.DataProvider.countryCode"));
  }

  @org.junit.Test
  public void createDPResponse() throws FileNotFoundException, JAXBException {
    TDETOOPResponseType dpResponse = ToopMessageCreator.createDPResponse(new FileInputStream("data/response/TOOPResponse.xml"));
    System.out.println(dpResponse.getRoutingInformation().getProcessIdentifier());
  }
}
