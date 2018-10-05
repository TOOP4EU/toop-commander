package eu.toop.commander.connector;

import eu.toop.commander.ToopMessageCreator;
import eu.toop.commons.dataexchange.TDETOOPRequestType;
import eu.toop.commons.dataexchange.TDETOOPResponseType;
import org.junit.jupiter.api.Test;

class ToopMessageCreatorTest {

  @Test
  void createDCRequest() throws Exception {
    TDETOOPRequestType dcRequest = ToopMessageCreator.createDCRequest("myid", "mycountry", "metadata.conf");
    byte[] bytes = ToopMessageCreator.serializeRequest(dcRequest);
    System.out.println(new String(bytes));
  }

  @Test
  void createDPResponse() throws Exception {
    TDETOOPResponseType dpResponse = ToopMessageCreator.createDPResponse("myid", "mycountry", "metadata.conf");
    byte[] bytes = ToopMessageCreator.serializeResponse(dpResponse);
    System.out.println(new String(bytes));
  }

}