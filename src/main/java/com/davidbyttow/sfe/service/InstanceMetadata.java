package com.davidbyttow.sfe.service;

/** Metadata about a running instance, as provided by the IaaS on-VM metadata service */
public interface InstanceMetadata {
  /** @return The name of the IaaS specific region in which the instance is running */
  String getRegion();

  /** @return The name of the instance */
  String getInstanceName();

  /** @return The platform on which the service is running */
  InstanceHostProvider getPlatform();
}
