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
package eu.toop.commander.util;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * A class that contains utility functions
 */
public class CommanderUtil {
  /**
   * Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(CommanderUtil.class);

  /**
   * Check if the first bytes of <code>src</code> match <code>probe</code>
   *
   * @param src byte array
   * @param probe byte array
   * @return <code>true</code> if they match, <code>false</code> if not.
   */
  public static boolean matchHeader(byte[] src, byte[] probe) {
    if (src == null || probe == null || src.length < probe.length)
      return false;

    for (int i = 0; i < probe.length; ++i) {
      if (src[i] != probe[i])
        return false;
    }

    return true;
  }

  /**
   * Try to read the given path name (possibly adding the extension <code>.conf</code> as a file or a resource.
   * <br><br>
   * <p>
   * First try a file with name <code>pathname + ".conf"</code> and then
   * if it doesn't exist, try the resource <code>pathname + ".conf"</code>. If the resource also does not exist,
   * then throw an Exception
   * </p>
   *
   * @param pathname the name of the config file to be parsed
   * @param includeSys a flag to indicate whether to include the System.properties or not.
   * @return the parsed Config object
   */
  public static Config resolveConfiguration(String pathname, boolean includeSys) {
    Config config;
    File file = new File(pathname);
    if (file.exists()) {
      LOGGER.info("Loading config from the file \"" + file.getName() + "\"");
      config = ConfigFactory.parseFile(file);
    } else {
      LOGGER.info("Loading config from the resource \"" + pathname);
      config = ConfigFactory.load(pathname);
    }

    if(includeSys){
      config = config.withFallback(ConfigFactory.systemProperties());
    }

    return config.resolve();
  }

  /**
   * Checks if the given path exists as a file, and load its input stream,
   * if not, then tries to load it from the classpath as "/" + path if it doesn't begin with a slash already
   * @param path
   * @return
   */
  public static InputStream loadFileOrResourceStream(String path) throws FileNotFoundException {
    ValueEnforcer.notEmpty(path, "file or resource path cannot be null");
    if(new File(path).exists())
      return new FileInputStream(path);
    else{
      if(LOGGER.isTraceEnabled()){
        LOGGER.trace("file " + path + " missing. Try classpath resource");
      }
      String resourcePath;
      if(!path.startsWith("/"))
        resourcePath = "/" + path;
      else
        resourcePath = path;

      //never mind the classpath, just try on the current class
      InputStream inputStream = CommanderUtil.class.getResourceAsStream(resourcePath);

      if(inputStream == null){
        //panic, not found
        throw new FileNotFoundException("A file [" + path + "] or classpath resource [" + resourcePath + "] was not found");
      }

      if(LOGGER.isTraceEnabled()){
        LOGGER.trace(resourcePath + " hit");
      }
      return inputStream;
    }
  }

  /**
   * Transfer the classpath resource to the current directory if it doesn't exist
   *
   * @param path the resource to be copied
   */
  public static void transferResourceToCurrentDirectory(String path) {
    ValueEnforcer.notEmpty(path, "The resource path");

    if (!path.startsWith("/")) {
      throw new IllegalStateException("Please provide a resource with an absolute path.");
    }

    String localPath = path.substring(1); //strip off the /
    File file = new File(localPath);

    if (!file.exists()) {

      //make sure that we have the directory path.
      File parentFolder = new File(file.getAbsolutePath()).getParentFile();

      if(!parentFolder.exists()){
        parentFolder.mkdirs();
      }

      try (FileOutputStream fileOutputStream = new FileOutputStream(file);
           InputStream inputStream = CommanderUtil.class.getResourceAsStream(path)) {

        //copy the stream
        StreamHelper.copyInputStreamToOutputStream(inputStream, fileOutputStream);

      } catch (Exception ex) {
        LOGGER.error("Failed to copy resource " + path + " to the local directroy.", ex);
      }
    }
  }

}
