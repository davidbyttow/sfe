package com.davidbyttow.sfe.environment.local;

/**
 * @author d
 */

import com.davidbyttow.sfe.service.InstanceHostProvider;
import com.davidbyttow.sfe.service.InstanceMetadata;

/** Metadata for local development instances */
public class LocalInstanceMetadata implements InstanceMetadata {
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
