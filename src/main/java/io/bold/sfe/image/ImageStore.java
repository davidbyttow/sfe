package io.bold.sfe.image;

import java.io.InputStream;

public interface ImageStore {
  class Result {
    public InputStream inputStream;
    public String contentType;
    public long contentLength;
  }

  void put(String key, String contentType, InputStream input, int contentLength);

  Result get(String key);
}
