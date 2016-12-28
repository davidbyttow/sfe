package com.davidbyttow.sfe.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.davidbyttow.sfe.common.StringEnum;
import com.davidbyttow.sfe.common.StringEnums;

import java.io.IOException;

public final class StringEnumJsonModule extends Module {
  @Override public String getModuleName() {
    return StringEnumJsonModule.class.getSimpleName();
  }

  @Override public Version version() {
    return PackageVersion.VERSION;
  }

  @Override public void setupModule(SetupContext context) {
    context.addDeserializers(new Deserializers.Base() {
      @Override
      public JsonDeserializer<?> findEnumDeserializer(final Class<?> type, DeserializationConfig config, BeanDescription bd)
          throws JsonMappingException {
        if (StringEnum.class.isAssignableFrom(type)) {
          return new JsonDeserializer<StringEnum>() {
            @Override
            @SuppressWarnings("unchecked")
            public StringEnum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
              String value = jp.getText();
              return StringEnums.valueOf((Class<? extends StringEnum>) type, value);
            }
          };
        }
        return null;
      }
    });

    context.addSerializers(new Serializers.Base() {
      @Override
      public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription bd) {
        if (StringEnum.class.isAssignableFrom(type.getRawClass())) {
          return new JsonSerializer<StringEnum>() {
            @Override
            public void serialize(StringEnum value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
              jgen.writeString(value.asString());
            }
          };
        }

        return null;
      }
    });
  }

}
