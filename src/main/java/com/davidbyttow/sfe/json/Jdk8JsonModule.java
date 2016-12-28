package com.davidbyttow.sfe.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.ser.Serializers;

import java.util.Optional;

public class Jdk8JsonModule extends Module {
  @Override public String getModuleName() {
    return "Jdk8JsonModule";
  }

  @Override public Version version() {
    return PackageVersion.VERSION;
  }

  @Override public void setupModule(SetupContext context) {
    context.addDeserializers(new Deserializers.Base() {
      @Override
      public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
                                                      BeanDescription beanDesc) throws JsonMappingException {
        Class<?> raw = type.getRawClass();
        if (Optional.class.isAssignableFrom(raw)) {
          JsonDeserializer<?> valueDeser = type.getValueHandler();
          TypeDeserializer typeDeser = type.getTypeHandler();
          return new OptionalDeserializer(type, typeDeser, valueDeser);
        }

        return super.findBeanDeserializer(type, config, beanDesc);
      }
    });

    context.addSerializers(new Serializers.Base() {
      @Override
      public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();
        if (Optional.class.isAssignableFrom(raw)) {
          return new OptionalSerializer(type);
        }
        return super.findSerializer(config, type, beanDesc);
      }
    });
  }
}
