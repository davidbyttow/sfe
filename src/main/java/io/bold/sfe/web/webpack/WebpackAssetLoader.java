package io.bold.sfe.web.webpack;

import com.google.inject.Inject;
import io.bold.sfe.environment.ResourceLoader;
import io.bold.sfe.web.LocalAssetLoader;

import java.io.InputStream;
import java.net.URL;

public class WebpackAssetLoader implements LocalAssetLoader {

  private final WebpackAssetResolver resolver;
  private final ResourceLoader resourceLoader;

  @Inject WebpackAssetLoader(WebpackAssetResolver resolver, ResourceLoader resourceLoader) {
    this.resolver = resolver;
    this.resourceLoader = resourceLoader;
  }

  @Override public InputStream open(String path) {
    return resourceLoader.open(resolver.getAssetFile(path));
  }

  @Override public URL getUrl(String path) {
    return resourceLoader.getUrl(resolver.getAssetFile(path));
  }
}
