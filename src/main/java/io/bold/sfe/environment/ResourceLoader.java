package io.bold.sfe.environment;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URL;

public interface ResourceLoader {
  @Nullable InputStream open(String path);

  URL getUrl(String path);
}
