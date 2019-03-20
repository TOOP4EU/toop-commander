/**
 * Copyright (C) 2018-2019 toop.eu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.commander;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.helger.xsds.ccts.cct.schemamodule.CodeType;
import eu.toop.commons.dataexchange.v140.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.UUIDType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.BinaryObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;

import eu.toop.commons.codelist.EPredefinedDocumentTypeIdentifier;
import eu.toop.commons.codelist.EPredefinedProcessIdentifier;
import eu.toop.commons.concept.ConceptValue;
import eu.toop.commons.exchange.ToopMessageBuilder140;
import eu.toop.commons.jaxb.ToopWriter;
import eu.toop.commons.jaxb.ToopXSDHelper140;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.IdentifierType;

public class ToopMessageCreator {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToopMessageCreator.class);

  private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  private static eu.toop.commons.dataexchange.v140.ObjectFactory v140Factory = new ObjectFactory();

  public static TDETOOPRequestType createDCRequest(String identifier, String country, String metadataFile) {
    LOGGER.debug("Create a DC request from  identifier " + identifier + " country : " + country);

    Config conf = parseMetadataFile(metadataFile);


    TDETOOPRequestType tdetoopRequestType = v140Factory.createTDETOOPRequestType();

    //DocumentUniversalUniqueIdentifier
    tdetoopRequestType.setDocumentUniversalUniqueIdentifier(
        ToopXSDHelper140.createIdentifier(conf.getString("ToopMessage.DocumentUniversalUniqueIdentifier.schemeAgencyID"),
            conf.getString("ToopMessage.DocumentUniversalUniqueIdentifier.schemeID"),
            UUID.randomUUID().toString())
    );

    //DocumentIssueDate - DocumentIssueTime
    GregorianCalendar c = new GregorianCalendar();
    try {
      XMLGregorianCalendar dateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
      tdetoopRequestType.setDocumentIssueDate(dateTime);
      tdetoopRequestType.setDocumentIssueTime(dateTime);
    } catch (DatatypeConfigurationException e) {
      LOGGER.error("Coudln't create request");
      throw new IllegalStateException(e);
    }

    //Copy indicator
    tdetoopRequestType.setCopyIndicator(ToopXSDHelper140.createIndicator(false));

    // DataConsumerDocumentIdentifier
    tdetoopRequestType.setDataConsumerDocumentIdentifier(ToopXSDHelper140.createIdentifier("demo-agency", "whatsoever", "DC-ID-17"));


    //   <DataConsumerCountryCode>SE</DataConsumerCountryCode>
    //   <DataProviderCountryCode>GQ</DataProviderCountryCode>
//

    String predefinedDocTypeId = EPredefinedDocumentTypeIdentifier.URN_EU_TOOP_NS_DATAEXCHANGE_1P40_REQUEST_URN_EU_TOOP_REQUEST_REGISTEREDORGANIZATION_1_40.getID();

    //SpecificationIdentifier
    tdetoopRequestType.setSpecificationIdentifier(ToopXSDHelper140.createIdentifier("toop-doctypeid-qns",
        predefinedDocTypeId.substring(0, predefinedDocTypeId.indexOf("##"))));


    TDERoutingInformationType routingInformation = v140Factory.createTDERoutingInformationType();
    routingInformation.setDocumentTypeIdentifier(ToopXSDHelper140.createIdentifier("toop-doctypeid-qns", predefinedDocTypeId));
    routingInformation.setProcessIdentifier(ToopXSDHelper140.createIdentifier("toop-procid-agreement", EPredefinedProcessIdentifier.DATAREQUESTRESPONSE.getID()));
    routingInformation.setDataConsumerElectronicAddressIdentifier(createParticipantId(conf));
    String dataConsumerCountryCode = getOrDefault(conf, "ToopMessage.RoutingInformation.DataConsumerCountryCode", "SE");
    String dataProviderCountryCode = getOrDefault(conf, "ToopMessage.RoutingInformation.DataProviderCountryCode", "GQ");
    routingInformation.setDataConsumerCountryCode(ToopXSDHelper140.createCode(dataConsumerCountryCode));
    routingInformation.setDataProviderCountryCode(ToopXSDHelper140.createCode(dataProviderCountryCode));
    tdetoopRequestType.setRoutingInformation(routingInformation);

    final TDEDataRequestSubjectType dataRequestSubjectType = new TDEDataRequestSubjectType();

    TDEDataConsumerType dataConsumerType = v140Factory.createTDEDataConsumerType();
    dataConsumerType.setDCUniqueIdentifier(ToopXSDHelper140.createIdentifier("whatsoever","9914", "ATU12345678"));
    dataConsumerType.setDCName(ToopXSDHelper140.createText("Helger Enterprises"));
    TDEAddressType tdeAddressType = v140Factory.createTDEAddressType();
    tdeAddressType.setCountryCode(ToopXSDHelper140.createCodeWithLOA(dataConsumerCountryCode));
    dataConsumerType.setDCLegalAddress(tdeAddressType);

    tdetoopRequestType.setDataConsumer(dataConsumerType);

    fillNaturalPersonProperties(dataRequestSubjectType, identifier, conf);
    //fillLegalPersonProperties(conf, dataRequestSubjectType);
    tdetoopRequestType.setDataRequestSubject(dataRequestSubjectType);

    TDEDataRequestAuthorizationType authorization = v140Factory.createTDEDataRequestAuthorizationType();
    authorization.setDataRequestConsentToken(new BinaryObjectType());


    tdetoopRequestType.setDataRequestAuthorization(authorization);


    fillConcepts(conf, tdetoopRequestType);


    return tdetoopRequestType;
  }


  public static TDETOOPResponseType createDPResponse(String identifier, String country, String metadataFile) {

    Config conf = parseMetadataFile(metadataFile);

    final List<ConceptValue> conceptList = new ArrayList<>();
    final TDEDataRequestSubjectType dataRequestSubjectType = new TDEDataRequestSubjectType();

    fillNaturalPersonProperties(dataRequestSubjectType, identifier, conf);
    fillLegalPersonProperties(conf, dataRequestSubjectType);
    //fillConcepts(conf, );
    IdentifierType participantID = createParticipantId(conf);

    return ToopMessageBuilder140.createMockResponse(participantID, dataRequestSubjectType, country, country,
        EPredefinedDocumentTypeIdentifier.RESPONSE_REGISTEREDORGANIZATION,
        EPredefinedProcessIdentifier.DATAREQUESTRESPONSE, conceptList);
  }

  public static TDETOOPResponseType createDPResponse(TDETOOPRequestType tdeToopRequestType, String metadataFile) {

    Config conf = parseMetadataFile(metadataFile);

    // Convert the TOOP Request to a TOOP Response
    final TDETOOPResponseType aResponse = new TDETOOPResponseType();
    tdeToopRequestType.cloneTo(aResponse);

    final TDEDataProviderType dataProviderType = new TDEDataProviderType();
    fillDataProviderProperties(conf, aResponse.getRoutingInformation(), dataProviderType);
    aResponse.getRoutingInformation().setDocumentTypeIdentifier(ToopXSDHelper140.createIdentifier(EPredefinedDocumentTypeIdentifier.RESPONSE_REGISTEREDORGANIZATION.getScheme(), EPredefinedDocumentTypeIdentifier.RESPONSE_REGISTEREDORGANIZATION.getID()));
    aResponse.addDataProvider(dataProviderType);

    String schemeId = conf.getString("ToopMessage.DataRequestIdentifier.schemeId");
    String schemeAgencyId = conf.getString("ToopMessage.DataRequestIdentifier.schemeAgencyId");
    String identifier = conf.getString("ToopMessage.DataRequestIdentifier.identifier");

    IdentifierType uuid = ToopXSDHelper140.createIdentifier(schemeId, identifier);
    uuid.setSchemeAgencyID(schemeAgencyId);
    aResponse.setDataRequestIdentifier(uuid);
    aResponse.getSpecificationIdentifier().setValue("urn:eu:toop:ns:dataexchange-1p40::Response");
    return aResponse;
  }

  public static Config parseMetadataFile(String metadataFile) {
    ConfigParseOptions opt = ConfigParseOptions.defaults();
    opt.setSyntax(ConfigSyntax.CONF);
    if (metadataFile == null)
      metadataFile = "metadata.conf";

    LOGGER.debug("Parse metadata file: " + metadataFile);

    File file = new File(metadataFile);
    if (!file.exists())
      throw new IllegalArgumentException("file " + metadataFile + " not found");

    return ConfigFactory.parseFile(file).resolve();
  }

  private static String getOrDefault(Config conf, String key, String defaultValue) {
    String value = conf.getString(key);

    if (value != null) {
      LOGGER.debug("Override default " + key + " value with " + defaultValue);
      value = defaultValue;
    }
    return value;
  }

  private static IdentifierType createParticipantId(Config conf) {
    String schemeAgencyId = conf.getString("ToopMessage.RoutingInformation.DataConsumerElectronicAddressIdentifier.schemeAgencyId");
    String schemeId = conf.getString("ToopMessage.RoutingInformation.DataConsumerElectronicAddressIdentifier.schemeId");
    String participandId = conf.getString("ToopMessage.RoutingInformation.DataConsumerElectronicAddressIdentifier.value");

    IdentifierType identifier = ToopXSDHelper140.createIdentifier(
        schemeAgencyId,
        schemeId,
        participandId);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(identifier.toString());
    }

    return identifier;
  }

  private static void fillConcepts(Config conf, TDETOOPRequestType request) {
    LOGGER.debug("Process concepts");
    final String conceptNamespace = conf.getString("ToopMessage.Concepts.conceptNamespace");
    LOGGER.info("Namepspace " + conceptNamespace);
    final List<String> conceptStringList = conf.getStringList("ToopMessage.Concepts.conceptList");
    for (String concept : conceptStringList) {
      LOGGER.trace("Concept " + concept);
      TDEDataElementRequestType tdeDataElementRequestType = v140Factory.createTDEDataElementRequestType();
      tdeDataElementRequestType.setDataElementRequestIdentifier(ToopXSDHelper140.createIdentifier("bla"));
      TDEConceptRequestType tdeConceptRequestType = v140Factory.createTDEConceptRequestType();
      tdeConceptRequestType.setConceptTypeCode(ToopXSDHelper140.createCode("DC"));
      tdeConceptRequestType.setSemanticMappingExecutionIndicator(ToopXSDHelper140.createIndicator(false));
      tdeConceptRequestType.setConceptNamespace(ToopXSDHelper140.createIdentifier(conceptNamespace));
      tdeConceptRequestType.setConceptName(ToopXSDHelper140.createText(concept));
      tdeConceptRequestType.setConceptDefinition(Arrays.asList(ToopXSDHelper140.createText("Definition of " + concept)));
      tdeDataElementRequestType.setConceptRequest(tdeConceptRequestType);
      request.addDataElementRequest(tdeDataElementRequestType);
    }
  }

  private static void fillNaturalPersonProperties(TDEDataRequestSubjectType dataRequestSubjectType, String identifier, Config conf) {
    final String dataSubjectTypeCode = conf.getString("ToopMessage.dataSubjectTypeCode");
    String naturalPersonIdentifier = conf.getString("ToopMessage.NaturalPerson.identifier");
    //check if identifier is provided and set it if yes
    if (identifier != null) {
      LOGGER.debug("Override natural person identifier with " + identifier);
      naturalPersonIdentifier = identifier;
    }
    dataRequestSubjectType.setDataRequestSubjectTypeCode(ToopXSDHelper140.createCode(dataSubjectTypeCode));
    {
      final String naturalPersonFirstName = conf.getString("ToopMessage.NaturalPerson.firstName");
      final String naturalPersonFamilyName = conf.getString("ToopMessage.NaturalPerson.familyName");
      final String naturalPersonBirthPlace = conf.getString("ToopMessage.NaturalPerson.birthPlace");
      final String naturalPersonNationality = conf.getString("ToopMessage.NaturalPerson.nationality");
      final TDENaturalPersonType naturalPerson = new TDENaturalPersonType();
      naturalPerson.setPersonIdentifier(ToopXSDHelper140.createIdentifierWithLOA(naturalPersonIdentifier));
      naturalPerson.setFamilyName(ToopXSDHelper140.createTextWithLOA(naturalPersonFamilyName));
      naturalPerson.setFirstName(ToopXSDHelper140.createTextWithLOA(naturalPersonFirstName));

      try {
        Date date = sdf.parse(conf.getString("ToopMessage.NaturalPerson.birthDate"));
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        naturalPerson.setBirthDate(ToopXSDHelper140.createDateWithLOA(date2));
      } catch (Exception e) {
        throw new IllegalArgumentException(e.getMessage(), e);
      }

      final TDEAddressWithLOAType aAddress = new TDEAddressWithLOAType();
      // Destination country to use
      aAddress.setStreetName(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.NaturalPerson.Address.streetName")));
      aAddress.setStreetNumber(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.NaturalPerson.Address.streetNumber")));
      aAddress.setCity(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.NaturalPerson.Address.city")));
      aAddress.setPostCode(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.NaturalPerson.Address.postCode")));
      aAddress.setCountry(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.NaturalPerson.Address.country")));
      aAddress.setCountryCode(ToopXSDHelper140.createCodeWithLOA(conf.getString("ToopMessage.NaturalPerson.Address.countryCode")));
      final List<String> addressLines = conf.getStringList("ToopMessage.NaturalPerson.Address.addressLines");
      if (addressLines != null)
        for (String line : addressLines) {
          aAddress.addAddressLine(ToopXSDHelper140.createTextWithLOA(line));
        }
      naturalPerson.setNaturalPersonLegalAddress(aAddress);

      dataRequestSubjectType.setNaturalPerson(naturalPerson);
    }
  }

  private static void fillLegalPersonProperties(Config conf, TDEDataRequestSubjectType dataRequestSubjectType) {
    final String legalPersonIdentifier = conf.getString("ToopMessage.LegalPerson.identifier");
    final String legalPersonName = conf.getString("ToopMessage.LegalPerson.name");
    final String legalPersonNationality = conf.getString("ToopMessage.LegalPerson.nationality");


    final TDELegalPersonType legalEntity = new TDELegalPersonType();
    legalEntity.setLegalPersonUniqueIdentifier(ToopXSDHelper140.createIdentifierWithLOA(legalPersonIdentifier));
    legalEntity.setLegalEntityIdentifier(ToopXSDHelper140.createIdentifierWithLOA(legalPersonIdentifier));
    legalEntity.setLegalName(ToopXSDHelper140.createTextWithLOA(legalPersonName));

    final TDEAddressWithLOAType aAddress = new TDEAddressWithLOAType();
    // Destination country to use
    aAddress.setStreetName(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.LegalPerson.Address.streetName")));
    aAddress.setStreetNumber(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.LegalPerson.Address.streetNumber")));
    aAddress.setCity(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.LegalPerson.Address.city")));
    aAddress.setPostCode(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.LegalPerson.Address.postCode")));
    aAddress.setCountry(ToopXSDHelper140.createTextWithLOA(conf.getString("ToopMessage.LegalPerson.Address.country")));
    aAddress.setCountryCode(ToopXSDHelper140.createCodeWithLOA(conf.getString("ToopMessage.LegalPerson.Address.countryCode")));
    final List<String> addressLines = conf.getStringList("ToopMessage.LegalPerson.Address.addressLines");
    if (addressLines != null)
      for (String line : addressLines) {
        aAddress.addAddressLine(ToopXSDHelper140.createTextWithLOA(line));
      }

    legalEntity.setLegalPersonLegalAddress(aAddress);
    dataRequestSubjectType.setLegalPerson(legalEntity);
  }

  private static void fillDataProviderProperties(final Config conf,
                                                 final TDERoutingInformationType routingInfo,
                                                 final TDEDataProviderType dataProviderType) {
    final String schemeId = conf.getString("ToopMessage.DataProvider.schemeId");
    final String schemeAgencyId = conf.getString("ToopMessage.DataProvider.schemeAgencyId");
    final String identifier = conf.getString("ToopMessage.DataProvider.identifier");
    final String name = conf.getString("ToopMessage.DataProvider.name");
    final String electronicAddressIdentifier = conf.getString("ToopMessage.DataProvider.electronicAddressIdentifier");
    final String countryCode = conf.getString("ToopMessage.DataProvider.countryCode");

    IdentifierType dpIdentifier = ToopXSDHelper140.createIdentifier(schemeId, identifier);
    dpIdentifier.setSchemeAgencyID(schemeAgencyId);
    dataProviderType.setDPIdentifier(dpIdentifier);
    dataProviderType.setDPName(ToopXSDHelper140.createText(name));
    routingInfo.setDataProviderElectronicAddressIdentifier(ToopXSDHelper140.createIdentifier(electronicAddressIdentifier));
    final TDEAddressType pa = new TDEAddressType();
    pa.setCountryCode(ToopXSDHelper140.createCodeWithLOA(countryCode));
    dataProviderType.setDPLegalAddress(pa);
  }

  public static byte[] serializeResponse(TDETOOPResponseType dpResponse) {
    return ToopWriter.response140().getAsBytes(dpResponse);
  }

  public static byte[] serializeRequest(TDETOOPRequestType dcRequest) {
    return ToopWriter.request140().getAsBytes(dcRequest);
  }
}
