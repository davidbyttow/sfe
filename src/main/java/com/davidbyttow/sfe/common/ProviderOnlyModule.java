package com.davidbyttow.sfe.common;

import com.google.inject.AbstractModule;

/** An {@link com.google.inject.AbstractModule} that binds everything via {@link com.google.inject.Provides} */
public abstract class ProviderOnlyModule extends AbstractModule {
  @Override protected void configure() {}
}
