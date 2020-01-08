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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.typesafe.config.Config;

/**
 * @author Anton Wiklund
 */
public class MetadataTest {

  @Test
  void metadataTest() {

    final String metadataFile = "data/request-metadata.conf";

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

    final String metadataFile = "data/response-metadata.conf";

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
