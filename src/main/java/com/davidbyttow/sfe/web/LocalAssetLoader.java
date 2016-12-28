package com.davidbyttow.sfe.web;

import java.io.InputStream;
import java.net.URL;

public interface LocalAssetLoader {
  InputStream open(String path);

  URL getUrl(String path);
}
