package com.davidbyttow.sfe.environment;

import com.davidbyttow.sfe.common.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URL;

public final class ClassLoaders {
  private ClassLoaders() {}

  private static final Logger log = LoggerFactory.getLogger(ClassLoaders.class);

  @Nullable public static InputStream open(String path) {
    InputStream stream = ClassLoaders.class.getClassLoader().getResourceAsStream(getResourcePath(path));
    if (stream == null) {
      log.warn("Could not load resource '{}'", path);
    }
    return stream;
  }

  public static URL getUrl(String path) {
    return ClassResourceLoader.class.getClassLoader().getResource(getResourcePath(path));
  }

  private static String getResourcePath(String path) {
    return Paths.withoutLeadingSlash(path);
  }
}
