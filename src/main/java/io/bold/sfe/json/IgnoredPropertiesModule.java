package io.bold.sfe.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import io.bold.sfe.storage.entity.Ignored;

public class IgnoredPropertiesModule extends Module {
  @Override public String getModuleName() {
    return IgnoredPropertiesModule.class.getSimpleName();
  }

  @Override public Version version() {
    return PackageVersion.VERSION;
  }

  @Override public void setupModule(SetupContext context) {
    context.insertAnnotationIntrospector(new AnnotationIntrospector() {
      @Override public boolean hasIgnoreMarker(AnnotatedMember am) {
        return am.hasAnnotation(Ignored.class);
      }

      @Override public Version version() {
        return PackageVersion.VERSION;
      }
    });
  }
}
