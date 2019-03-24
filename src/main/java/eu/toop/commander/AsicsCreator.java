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

import com.helger.asic.AsicWriterFactory;
import com.helger.asic.ESignatureMethod;
import com.helger.asic.IAsicWriter;
import com.helger.asic.SignatureHelper;
import com.helger.commons.mime.EMimeContentType;
import com.helger.commons.mime.IMimeType;
import com.helger.commons.mime.MimeType;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.security.keystore.EKeyStoreType;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;

/**
 * This class is used for creating the asic files from the sample request and response XML files
 */
public class AsicsCreator {
  static AsicWriterFactory factory = AsicWriterFactory.newFactory(ESignatureMethod.XAdES);
  static SignatureHelper signatureHelper = new SignatureHelper(EKeyStoreType.PKCS12,
      "./toop-commander.pfx",
      "123456",
      "toop-commander",
      "123456");

  /**
   * Entry point
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    createAsic("samples/request/TOOPRequest.asice", "samples/request/TOOPRequest.xml", "TOOPRequest");
    createAsic("samples/response/TOOPResponse.asice", "samples/response/TOOPResponse.xml", "TOOPResponse");
  }

  /**
   * Create an asic file from the <code>input</code> file, place it into the <code>target</code> asic file
   * and set the entry name to <code>name</code>
   * @param target the asic file to be created
   * @param input the input xml file
   * @param name the name of the zip entry
   * @throws IOException
   */
  private static void createAsic(String target, String input, String name) throws IOException {
    IAsicWriter iAsicWriter = factory.newContainer(new File(target));
    iAsicWriter.add(new File(input), name, MimeTypeParser.parseMimeType("application/xml"));
    iAsicWriter.sign(signatureHelper);
  }
}
