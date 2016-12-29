package io.bold.sfe.common;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/** Helpers for retrieving a concrete {@link StringEnum} from it's encoded value */
public final class StringEnums {
  private static final LoadingCache<Class<? extends StringEnum>, Map<String, StringEnum>> STRING_ENUM_VALUES =
      CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends StringEnum>, Map<String, StringEnum>>() {
        @SuppressWarnings("unchecked")
        @Override public Map<String, StringEnum> load(Class<? extends StringEnum> key) throws Exception {
          Preconditions.checkState(key.isEnum(), "%s is not an enum", key.getName());
          ImmutableMap.Builder<String, StringEnum> values = ImmutableMap.builder();
          for (Enum e : ((Class<? extends Enum<?>>) key).getEnumConstants()) {
            @SuppressWarnings("unchecked")
            StringEnum se = (StringEnum) e;
            values.put(se.asString(), se);
          }
          return values.build();
        }
      });

  @SuppressWarnings("unchecked")
  public static <T extends StringEnum> T valueOf(Class<T> enumType, String s) {
    T value = valueOf(enumType, s, null);
    Preconditions.checkArgument(value != null, "'%s' is not a valid %s", s, enumType.getName());
    return value;
  }

  @SuppressWarnings("unchecked")
  public static <T extends StringEnum> T valueOf(Class<T> enumType, String s, T defaultValue) {
    try {
      Map<String, StringEnum> enumValues = STRING_ENUM_VALUES.get(enumType);
      T value = (T) enumValues.get(s);
      return value == null ? defaultValue : value;
    } catch (ExecutionException e) {
      throw MoreThrowables.unwrapAndPropagate(e);
    }
  }


  private StringEnums() {}
}
