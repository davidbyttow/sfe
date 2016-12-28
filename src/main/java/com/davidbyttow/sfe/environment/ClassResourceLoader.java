package com.davidbyttow.sfe.environment;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.InputStream;
import java.net.URL;

public final class ClassResourceLoader implements ResourceLoader {

  @Inject public ClassResourceLoader() {}

  @Nullable @Override public InputStream open(String path) {
    return ClassLoaders.open(path);
  }

  @Override public URL getUrl(String path) {
    return ClassLoaders.getUrl(path);
  }
}
