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
package eu.toop.commander.util;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class that contains utility functions
 */
public class Util {
  /**
   * Logger instance
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

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
   * <p>Try to parse and resolve the given URL as a HOCON resource. If <code>includeSys == true</code> then
   * a System Properties are also added as fallback</p>
   *
   * @param url the url of the config to be parsed
   * @param includeSys a flag to indicate whether to include the System.properties or not.
   * @return the parsed Config object
   */
  public static Config resolveConfiguration(URL url, boolean includeSys) {
    Config config;

    LOGGER.info("Loading config from the URL \"" + url);
    config = ConfigFactory.parseURL(url);

    if (includeSys) {
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
    if (new File(path).exists())
      return new FileInputStream(path);
    else {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("file " + path + " missing. Try classpath resource");
      }
      String resourcePath;
      if (!path.startsWith("/"))
        resourcePath = "/" + path;
      else
        resourcePath = path;

      //never mind the classpath, just try on the current class
      InputStream inputStream = Util.class.getResourceAsStream(resourcePath);

      if (inputStream == null) {
        //panic, not found
        throw new FileNotFoundException("A file [" + path + "] or classpath resource [" + resourcePath + "] was not found");
      }

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(resourcePath + " hit");
      }
      return inputStream;
    }
  }

  /**
   * Transfer the classpath resource to the provided directory if it doesn't already exist there
   *
   * @param path the resource to be copied
   */
  public static void transferResourceToDirectory(String path, String targetDirName) {
    ValueEnforcer.notEmpty(path, "The resource path");
    ValueEnforcer.notEmpty(targetDirName, "The target directory");

    //the resource is already an absolute path. So remove a "/" if it exists
    if (path.startsWith("/"))
      path = path.substring(1);

    URL resource = Util.class.getClassLoader().getResource(path);

    if (resource == null)
      throw new IllegalArgumentException("Couldn't find the resource " + path);

    String resourceName = FilenameUtils.getName(path);

    File targetDir = new File(targetDirName);

    File targetFile = new File(targetDir, resourceName);

    //if a file with the same name doesn't exist in the target dir, then create one and transfer the resource
    if (!targetFile.exists()) {
      //try to create the path
      targetDir.mkdirs();

      try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
           InputStream inputStream = resource.openStream()) {

        //copy the stream
        StreamHelper.copyInputStreamToOutputStream(inputStream, fileOutputStream);

      } catch (Exception ex) {
        LOGGER.error("Failed to copy resource " + path + " to the local directroy.", ex);
      }
    }
  }

  /**
   * <p>If the file with name <code>pathName</code> exists then its path is returned as an URL<br><br>otherwise,
   * it tries to find the resource with name <code>pathName</code> on the classpath and load its URL and return</p>
   * @param pathName the file or classpath resource for which an URL is be resolved.
   * @return the resolved URL
   */
  public static URL getFileOrResourceAsURL(String pathName) {
    URL url;

    File file = new File(pathName);
    if (file.exists()) {
      try {
        url = file.toURI().toURL();
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Invalid URL \"" + pathName + "\" [MalformedURLException " + e.getMessage() + "]");
      }
    } else {
      url = Util.class.getClassLoader().getResource(pathName);
    }
    return url;
  }
}
