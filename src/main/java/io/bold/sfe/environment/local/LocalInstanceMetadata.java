package io.bold.sfe.environment.local;

/**
 * @author d
 */

import io.bold.sfe.service.InstanceHostProvider;
import io.bold.sfe.service.InstanceMetadata;

/** Metadata for local development instances */
public class LocalInstanceMetadata implements InstanceMetadata {

  @Override public boolean isLocal() { return true; }

  @Override public String getRegion() {
    return "local";
  }

  @Override public String getInstanceName() {
    return "local";
  }

  @Override public InstanceHostProvider getPlatform() {
    return InstanceHostProvider.LOCAL;
  }
}
