package io.bold.sfe.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.json.JsonSanitizer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

public class DataSerializer {
  private final ObjectMapper objectMapper;

  @Inject public DataSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Map<String, Object> serializeToMap(@Nullable Object data) throws IOException {
    if (data == null) {
      return ImmutableMap.of();
    }

    return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>(){});
  }

  public String serializeToString(@Nullable Object data) throws IOException {
    if (data == null) {
      return "null";
    }
    return sanitize(objectMapper.writeValueAsString(data));
  }

  private String sanitize(String unsafe) {
    return JsonSanitizer.sanitize(unsafe);
  }
}
