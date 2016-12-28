package com.davidbyttow.sfe.async;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.davidbyttow.sfe.concurrent.BackgroundThreadPool;

public class BarrierProvider implements Provider<Barrier> {

  private final ListeningExecutorService executor;

  @Inject BarrierProvider(@BackgroundThreadPool ListeningExecutorService executor) {
    this.executor = executor;
  }

  @Override public Barrier get() {
    return new Barrier(executor);
  }
}
