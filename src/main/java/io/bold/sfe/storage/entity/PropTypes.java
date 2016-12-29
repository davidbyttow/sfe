package io.bold.sfe.storage.entity;

import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

public final class PropTypes {

  private static final ImmutableMap<Class<?>, Prop.Type> TYPE_MAP = ImmutableMap.<Class<?>, Prop.Type>builder()
      .put(Boolean.TYPE, Prop.Type.Boolean)
      .put(Boolean.class, Prop.Type.Boolean)
      .put(Character.TYPE, Prop.Type.Integer)
      .put(Character.class, Prop.Type.Integer)
      .put(Short.TYPE, Prop.Type.Integer)
      .put(Short.class, Prop.Type.Integer)
      .put(Integer.TYPE, Prop.Type.Integer)
      .put(Integer.class, Prop.Type.Integer)
      .put(Long.TYPE, Prop.Type.Long)
      .put(Long.class, Prop.Type.Long)
      .put(Float.TYPE, Prop.Type.Float)
      .put(Float.class, Prop.Type.Float)
      .put(Double.TYPE, Prop.Type.Float)
      .put(Double.class, Prop.Type.Float)
      .put(String.class, Prop.Type.String)
      .put(DateTime.class, Prop.Type.DateTime)
      .build();

  @Nullable public static Prop.Type fromType(Class<?> type) {
    if (type.isEnum()) {
      return Prop.Type.Enum;
    }
    return TYPE_MAP.get(type);
  }

  private PropTypes() {}
}
