package com.davidbyttow.sfe.encoding;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import java.io.IOException;
import java.util.Map;

public class JsonProtoSerializer<T extends Message> extends JsonSerializer<T> {
  @Override public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : value.getAllFields().entrySet()) {
      Descriptors.FieldDescriptor descriptor = entry.getKey();
      Object fieldValue = entry.getValue();
    }
  }
}
