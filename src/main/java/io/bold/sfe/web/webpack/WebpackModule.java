package io.bold.sfe.web.webpack;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import io.bold.sfe.json.Json;
import io.bold.sfe.service.Env;
import io.bold.sfe.web.LocalAssetLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebpackModule extends AbstractModule {

  private static final Logger log = LoggerFactory.getLogger(WebpackModule.class);

  private final Pattern FINGERPRINT_DIR_PATTERN = Pattern.compile("^/fp/\\d{4}-\\d{2}(/.+)");

  @Override protected void configure() {
    bind(LocalAssetLoader.class).to(WebpackAssetLoader.class);
  }

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  private @interface Private {}

  private final String manifestFile;

  WebpackModule(String manifestFile) {
    this.manifestFile = manifestFile;
  }

  private static Map<String, String> aliasMap;

  @Provides
  @Private
  Map<String, String> aliasMap(Env env) {
    boolean isDev = env.isLocalDevelopment();
    if (!isDev && aliasMap != null) {
      return aliasMap;
    }
    InputStream stream = getClass().getResourceAsStream(manifestFile);
    if (stream == null) {
      if (!isDev) {
        throw new IllegalStateException("asset file missing: " + manifestFile);
      }
      log.warn("Asset file missing: {}. Will attempt to load on-demand", manifestFile);
      return new HashMap<>();
    }

    Map<String, String> rawMap = Json.readValue(stream, new TypeReference<Map<String, String>>() {});
    ImmutableMap.Builder<String, String>  builder = ImmutableMap.builder();
    for (Map.Entry<String, String> entry : rawMap.entrySet()) {
      Matcher matcher = FINGERPRINT_DIR_PATTERN.matcher(entry.getKey());
      if (matcher.matches()) {
        String requestPart = matcher.group(1);
        builder.put(requestPart, entry.getValue());
      } else {
        builder.put(entry.getKey(), entry.getValue());
      }
    }
    aliasMap = builder.build();
    return aliasMap;
  }

  @Provides WebpackAssetResolver mappedFiles(@Private Map<String, String> aliasMap) {
    return new WebpackAssetResolver(aliasMap);
  }
}
