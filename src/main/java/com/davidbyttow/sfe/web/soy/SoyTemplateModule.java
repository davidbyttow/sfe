package com.davidbyttow.sfe.web.soy;

import com.google.inject.Provides;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import com.davidbyttow.sfe.common.ProviderOnlyModule;
import com.davidbyttow.sfe.service.Env;
import com.davidbyttow.sfe.web.LocalAssetLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SoyTemplateModule extends ProviderOnlyModule {
  private static final Logger log = LoggerFactory.getLogger(SoyTemplateModule.class);

  private final List<String> soyFiles;

  public SoyTemplateModule(List<String> soyFiles) {
    this.soyFiles = soyFiles;
  }

  private static SoyTofu inst = null;

  // TODO(d): Need a better mechanism for this
  @Provides SoyTofu provideSoyTofu(LocalAssetLoader loader, Env env) throws IOException {
    if (env.isLocalDevelopment()) {
      return createSoyTofu(loader);
    }
    if (inst == null) {
      inst = createSoyTofu(loader);
    }
    return inst;
  }

  private SoyTofu createSoyTofu(LocalAssetLoader loader) {
    SoyFileSet.Builder fileSet = SoyFileSet.builder();
    for (String f : soyFiles) {
      log.debug("Loading soy template {}", f);
      fileSet.add(loader.getUrl(f));
    }
    return fileSet.build().compileToTofu();
  }

}
