package com.davidbyttow.sfe.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.google.common.base.Throwables;
import io.dropwizard.jackson.Jackson;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/** Helpers for dealing with Json */
public final class Json {
  private static final ObjectMapper OBJECT_MAPPER = newObjectMapper();

  public static void writeValue(OutputStream os, Object o) {
    try {
      OBJECT_MAPPER.writeValue(os, o);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static byte[] writeValueAsBytes(Object o) {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(o);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static String writeValueAsString(Object o) {
    try {
      return OBJECT_MAPPER.writeValueAsString(o);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static <T> T readValue(String s, Class<T> type) {
    try {
      return OBJECT_MAPPER.readValue(s, type);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static <T> T readValue(String s, TypeReference<T> type) {
    try {
      return OBJECT_MAPPER.readValue(s, type);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static <T> T readValue(byte[] b, Class<T> type) {
    try {
      return OBJECT_MAPPER.readValue(b, type);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static <T> T readValue(byte[] b, TypeReference<T> type) {
    try {
      return OBJECT_MAPPER.readValue(b, type);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static <T> T readValue(InputStream is, Class<T> type) {
    try {
      return OBJECT_MAPPER.readValue(is, type);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static <T> T readValue(InputStream is, TypeReference<T> type) {
    try {
      return OBJECT_MAPPER.readValue(is, type);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  // TODO(d): Make this far more efficient.
  public static <T> T readValue(Object object, TypeReference<T> type) {
    String jsonString = writeValueAsString(object);
    return readValue(jsonString, type);
  }

  // TODO(d): Make this far more efficient.
  public static <T> T readValue(@Nullable Object object, Class<T> type) {
    if (object == null) {
      try {
        return type.newInstance();
      } catch (Exception e) {
        throw Throwables.propagate(e);
      }
    }
    if (type.isInstance(object)) {
      return type.cast(object);
    }

    String jsonString = writeValueAsString(object);
    return readValue(jsonString, type);
  }


  public static String forLogging(Object o) {
    // NB(matt): So we can change this to smarter things later
    return writeValueAsString(o);
  }

  public static String prettyPrint(Object o) {
    try {
      return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static JSONObject toJSONObject(Map<String, String> map) {
    JSONObject obj = new JSONObject();
    for (Map.Entry<String, String> entry : map.entrySet()) {
      try {
        obj.put(entry.getKey(), entry.getValue());
      } catch (JSONException e) {
        throw Throwables.propagate(e);
      }
    }
    return obj;
  }

  public static ObjectMapper getObjectMapper() {
    return OBJECT_MAPPER;
  }

  /** @return A new preconfigured {@link ObjectMapper} */
  public static ObjectMapper newObjectMapper() {
    return configureMapper(Jackson.newObjectMapper());
  }

  /** Configures the default object mapper with desired defaults and modules */
  public static ObjectMapper configureMapper(ObjectMapper mapper) {
    mapper.registerModule(new Jdk8JsonModule());
    mapper.registerModule(new JsonOrgModule());
    mapper.registerModule(new StringEnumJsonModule());
    mapper.registerModule(new IgnoredPropertiesModule());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.setVisibilityChecker(mapper.getVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
    );

    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    return mapper;
  }

  private Json() {}
}
