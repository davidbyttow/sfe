package com.davidbyttow.sfe.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Optional;

public class OptionalSerializer extends StdSerializer<Optional<?>> {
  public OptionalSerializer(JavaType type) {
    super(type);
  }

  @Override
  public boolean isEmpty(Optional<?> value) {
    return (value == null) || !value.isPresent();
  }

  @Override
  public void serialize(Optional<?> value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    if (value.isPresent()) {
      provider.defaultSerializeValue(value.get(), jgen);
    } else {
      provider.defaultSerializeNull(jgen);
    }
  }

  @Override
  public void serializeWithType(Optional<?> value, JsonGenerator jgen, SerializerProvider provider,
                                TypeSerializer typeSer) throws IOException {
    serialize(value, jgen, provider);
  }

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
    JavaType typeParameter = typeHint.containedType(0);
    if (typeParameter != null) {
      visitor.getProvider().findValueSerializer(typeParameter, null).acceptJsonFormatVisitor(visitor, typeParameter);
      return;
    }
    super.acceptJsonFormatVisitor(visitor, typeHint);
  }
}
