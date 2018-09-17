package eu.toop.commander.connector;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import eu.toop.commons.codelist.EPredefinedDocumentTypeIdentifier;
import eu.toop.commons.codelist.EPredefinedProcessIdentifier;
import eu.toop.commons.concept.ConceptValue;
import eu.toop.commons.dataexchange.*;
import eu.toop.commons.exchange.ToopMessageBuilder;
import eu.toop.commons.jaxb.ToopXSDHelper;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.CodeType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.IdentifierType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.TextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ToopMessageCreator {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorManager.class);

  private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  public static TDETOOPRequestType createDCRequest(String identifier, String country, String metadataFile) {
    LOGGER.debug("Create a DC request from  identifier " + identifier + " country : " + country);

    Config conf = parseMetadataFile(metadataFile);

    final List<ConceptValue> conceptList = new ArrayList<>();
    final TDEDataRequestSubjectType dataRequestSubjectType = new TDEDataRequestSubjectType();

    fillNaturalPersonProperties(dataRequestSubjectType, identifier, conf);
    fillLegalPersonProperties(conf, dataRequestSubjectType);
    fillConcepts(conf, conceptList);
    IdentifierType participantID = createParticipantId(conf);
    String destinationCountryCode = createCountryCode(country, conf);

    return ToopMessageBuilder.createMockRequest(dataRequestSubjectType, participantID,
        destinationCountryCode, EPredefinedDocumentTypeIdentifier.REQUEST_REGISTEREDORGANIZATION,
        EPredefinedProcessIdentifier.DATAREQUESTRESPONSE, conceptList);
  }


  public static TDETOOPResponseType createDPResponse(String identifier, String country, String metadataFile) {

    Config conf = parseMetadataFile(metadataFile);

    final List<ConceptValue> conceptList = new ArrayList<>();
    final TDEDataRequestSubjectType dataRequestSubjectType = new TDEDataRequestSubjectType();

    fillNaturalPersonProperties(dataRequestSubjectType, identifier, conf);
    fillLegalPersonProperties(conf, dataRequestSubjectType);
    fillConcepts(conf, conceptList);
    IdentifierType participantID = createParticipantId(conf);
    String destinationCountryCode = createCountryCode(country, conf);

    return ToopMessageBuilder.createMockResponse(participantID,
        destinationCountryCode, EPredefinedDocumentTypeIdentifier.RESPONSE_REGISTEREDORGANIZATION,
        EPredefinedProcessIdentifier.DATAREQUESTRESPONSE, conceptList);
  }

  private static String createCountryCode(String country, Config conf) {
    String destinationCountryCode = conf.getString("ToopMessage.DestinationCountryCode");

    if (country != null) {
      LOGGER.debug("Override default destination country with " + country);
      destinationCountryCode = country;
    }
    return destinationCountryCode;
  }

  private static Config parseMetadataFile(String metadataFile) {
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

  private static IdentifierType createParticipantId(Config conf) {
    String schemeId = conf.getString("ToopMessage.SenderParticipantId.schemeId");
    String participandId = conf.getString("ToopMessage.SenderParticipantId.value");

    IdentifierType identifier = ToopXSDHelper.createIdentifier(
        //scheme id
        schemeId,
        //value
        participandId);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(identifier.toString());
    }

    return identifier;
  }

  private static void fillConcepts(Config conf, List<ConceptValue> conceptList) {
    LOGGER.debug("Process concepts");
    final String conceptNamespace = conf.getString("ToopMessage.Concepts.conceptNamespace");
    final List<String> conceptStringList = conf.getStringList("ToopMessage.Concepts.conceptList");
    for (String concept : conceptStringList) {
      LOGGER.trace("Concept " + concept);
      conceptList.add(new ConceptValue(conceptNamespace, concept));
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
    dataRequestSubjectType.setDataRequestSubjectTypeCode(ToopXSDHelper.createCode(dataSubjectTypeCode));
    {
      final String naturalPersonFirstName = conf.getString("ToopMessage.NaturalPerson.firstName");
      final String naturalPersonFamilyName = conf.getString("ToopMessage.NaturalPerson.familyName");
      final String naturalPersonBirthPlace = conf.getString("ToopMessage.NaturalPerson.birthPlace");
      final String naturalPersonNationality = conf.getString("ToopMessage.NaturalPerson.nationality");
      final TDENaturalPersonType naturalPerson = new TDENaturalPersonType();
      naturalPerson.setPersonIdentifier(ToopXSDHelper.createIdentifier(naturalPersonIdentifier));
      naturalPerson.setFamilyName(ToopXSDHelper.createText(naturalPersonFamilyName));
      naturalPerson.setFirstName(ToopXSDHelper.createText(naturalPersonFirstName));

      try {
        Date date = sdf.parse(conf.getString("ToopMessage.NaturalPerson.birthDate"));
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        naturalPerson.setBirthDate(date2);
      } catch (Exception e) {
        throw new IllegalArgumentException(e.getMessage(), e);
      }

      final TDEAddressType aAddress = new TDEAddressType();
      // Destination country to use
      aAddress.setStreetName(new TextType(conf.getString("ToopMessage.NaturalPerson.Address.streetName")));
      aAddress.setStreetNumber(new TextType(conf.getString("ToopMessage.NaturalPerson.Address.streetNumber")));
      aAddress.setCity(new TextType(conf.getString("ToopMessage.NaturalPerson.Address.city")));
      aAddress.setPostCode(new TextType(conf.getString("ToopMessage.NaturalPerson.Address.postCode")));
      aAddress.setCountry(new TextType(conf.getString("ToopMessage.NaturalPerson.Address.country")));
      aAddress.setCountryCode(new CodeType(conf.getString("ToopMessage.NaturalPerson.Address.countryCode")));
      final List<String> addressLines = conf.getStringList("ToopMessage.NaturalPerson.Address.addressLines");
      if (addressLines != null)
        for (String line : addressLines) {
          aAddress.addAddressLine(new TextType(line));
        }
      naturalPerson.setNaturalPersonLegalAddress(aAddress);

      dataRequestSubjectType.setNaturalPerson(naturalPerson);
    }
  }

  private static void fillLegalPersonProperties(Config conf, TDEDataRequestSubjectType dataRequestSubjectType) {
    final String legalPersonIdentifier = conf.getString("ToopMessage.LegalPerson.identifier");
    final String legalPersonName = conf.getString("ToopMessage.LegalPerson.name");
    final String legalPersonNationality = conf.getString("ToopMessage.LegalPerson.nationality");


    final TDELegalEntityType legalEntity = new TDELegalEntityType();
    legalEntity.setLegalPersonUniqueIdentifier(ToopXSDHelper.createIdentifier(legalPersonIdentifier));
    legalEntity.setLegalEntityIdentifier(ToopXSDHelper.createIdentifier(legalPersonIdentifier));
    legalEntity.setLegalName(ToopXSDHelper.createText(legalPersonName));

    final TDEAddressType aAddress = new TDEAddressType();
    // Destination country to use
    aAddress.setStreetName(new TextType(conf.getString("ToopMessage.LegalPerson.Address.streetName")));
    aAddress.setStreetNumber(new TextType(conf.getString("ToopMessage.LegalPerson.Address.streetNumber")));
    aAddress.setCity(new TextType(conf.getString("ToopMessage.LegalPerson.Address.city")));
    aAddress.setPostCode(new TextType(conf.getString("ToopMessage.LegalPerson.Address.postCode")));
    aAddress.setCountry(new TextType(conf.getString("ToopMessage.LegalPerson.Address.country")));
    aAddress.setCountryCode(new CodeType(conf.getString("ToopMessage.LegalPerson.Address.countryCode")));
    final List<String> addressLines = conf.getStringList("ToopMessage.LegalPerson.Address.addressLines");
    if (addressLines != null)
      for (String line : addressLines) {
        aAddress.addAddressLine(new TextType(line));
      }

    legalEntity.setLegalEntityLegalAddress(aAddress);
    dataRequestSubjectType.setLegalEntity(legalEntity);
  }

  public static byte[] serializeResponse(TDETOOPResponseType dpResponse) {
    ObjectFactory objectFactory = new ObjectFactory();

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(TDETOOPResponseType.class);
      Marshaller marshaller = jaxbContext.createMarshaller();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(objectFactory.createResponse(dpResponse), baos);


      return baos.toByteArray();
    } catch (Exception ex) {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }

  public static byte[] serializeRequest(TDETOOPRequestType dcRequest) {
    ObjectFactory objectFactory = new ObjectFactory();

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(TDETOOPRequestType.class);
      Marshaller marshaller = jaxbContext.createMarshaller();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(objectFactory.createRequest(dcRequest), baos);

      return baos.toByteArray();
    } catch (Exception ex) {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
}
