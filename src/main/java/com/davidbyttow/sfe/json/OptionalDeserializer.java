package com.davidbyttow.sfe.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.util.Optional;

public class OptionalDeserializer extends StdDeserializer<Optional<?>>
    implements ContextualDeserializer {
  private static final long serialVersionUID = 1L;

  protected final JavaType referenceType;
  protected final JsonDeserializer<?> valueDeserializer;
  protected final TypeDeserializer valueTypeDeserializer;

  public OptionalDeserializer(JavaType valueType, TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
    super(valueType);
    referenceType = valueType.containedType(0);
    valueTypeDeserializer = typeDeser;
    valueDeserializer = valueDeser;
  }

  @Override
  public Optional<?> getNullValue() {
    return Optional.empty();
  }

  /**
   * Method called to finalize setup of this deserializer,
   * after deserializer itself has been registered. This
   * is needed to handle recursive and transitive dependencies.
   */
  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
                                              BeanProperty property) throws JsonMappingException {
    JsonDeserializer<?> deser = valueDeserializer;
    TypeDeserializer typeDeser = valueTypeDeserializer;
    if (deser == null) {
      deser = ctxt.findContextualValueDeserializer(referenceType, property);
    }

    if (typeDeser != null) {
      typeDeser = typeDeser.forProperty(property);
    }

    if (deser == valueDeserializer && typeDeser == valueTypeDeserializer) {
      return this;
    }

    return new OptionalDeserializer(referenceType, typeDeser, deser);
  }

  @Override
  public Optional<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    Object refd;

    if (valueTypeDeserializer == null) {
      refd = valueDeserializer.deserialize(jp, ctxt);
    } else {
      refd = valueDeserializer.deserializeWithType(jp, ctxt, valueTypeDeserializer);
    }
    return Optional.of(refd);
  }

  @Override
  public Optional<?> deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
      throws IOException {
    final JsonToken t = jp.getCurrentToken();
    if (t == JsonToken.VALUE_NULL) {
      return getNullValue();
    }
    // 03-Nov-2013, tatu: This gets rather tricky with "natural" types
    //   (String, Integer, Boolean), which do NOT include type information.
    //   These might actually be handled ok except that nominal type here
    //   is `Optional`, so special handling is not invoked; instead, need
    //   to do a work-around here.
    if (t != null && t.isScalarValue()) {
      return deserialize(jp, ctxt);
    }
    // with type deserializer to use here? Looks like we get passed same one?
    Object ref = typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    return Optional.of(ref);
  }
}
