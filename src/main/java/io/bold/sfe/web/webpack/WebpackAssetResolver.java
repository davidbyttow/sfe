package io.bold.sfe.web.webpack;

import java.util.Map;

public class WebpackAssetResolver {

  private final Map<String, String> mappedFiles;

  WebpackAssetResolver(Map<String, String> mappedFiles) {
    this.mappedFiles = mappedFiles;
  }

  public String getAssetFile(String file) {
    String remapped = mappedFiles.get(file);
    return (remapped == null) ? file : remapped;
  }
}
