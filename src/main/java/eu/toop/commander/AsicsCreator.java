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
    createAsic("samples/request/TOOPRequest.asice", "samples/request/TOOPRequest.xml");
    createAsic("samples/response/TOOPResponse.asice", "samples/response/TOOPResponse.xml");
  }

  private static void createAsic(String target, String input) throws IOException {
    IAsicWriter iAsicWriter = factory.newContainer(new File(target));
    iAsicWriter.add(new File(input));
    iAsicWriter.sign(signatureHelper);
  }
}
