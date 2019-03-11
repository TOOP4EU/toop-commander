package eu.toop.commander;

import com.helger.asic.AsicWriterFactory;
import com.helger.asic.ESignatureMethod;
import com.helger.asic.IAsicWriter;
import com.helger.asic.SignatureHelper;
import com.helger.security.keystore.EKeyStoreType;

import java.io.File;
import java.io.IOException;

public class AsicsCreator {
  static AsicWriterFactory factory = AsicWriterFactory.newFactory(ESignatureMethod.XAdES);
  static SignatureHelper signatureHelper = new SignatureHelper(EKeyStoreType.PKCS12,
      "./toop-commander.pfx",
      "123456",
      "toop-commander",
      "123456");

  public static void main(String[] args) throws IOException {
    createAsic("samples/request/TOOPRequest.asic", "samples/request/TOOPRequest.xml");
    createAsic("samples/response/TOOPResponse.asic", "samples/response/TOOPResponse.xml");
  }

  private static void createAsic(String target, String input) throws IOException {
    IAsicWriter iAsicWriter = factory.newContainer(new File(target));
    iAsicWriter.add(new File(input));
    iAsicWriter.sign(signatureHelper);
  }
}
