package com.davidbyttow.sfe.environment;

import com.google.common.base.Throwables;
import com.davidbyttow.sfe.common.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class FileResourceLoader implements ResourceLoader {

  private static final Logger log = LoggerFactory.getLogger(FileResourceLoader.class);

  // Assume that the working directory is in front of resources
  private static final String BASE_DIR = "resources";

  @Inject public FileResourceLoader() {}

  @Nullable @Override public InputStream open(String path) {
    File file = getFile(path);
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      log.warn("Could not open file '{}'", file.getPath());
      return null;
    }
  }

  @Override public URL getUrl(String path) {
    File file = getFile(path);
    try {
      return file.toURI().toURL();
    } catch (MalformedURLException e) {
      log.error("Could not open file '{}'", file.getPath());
      throw Throwables.propagate(e);
    }
  }

  private File getFile(String path) {
    return Files.join(BASE_DIR, path);
  }
}
