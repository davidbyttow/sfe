package io.bold.sfe.testing;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class ImmediateExecutor extends AbstractExecutorService {
  @Override public void shutdown() {}

  @Override public List<Runnable> shutdownNow() {
    return ImmutableList.of();
  }

  @Override public boolean isShutdown() {
    return false;
  }

  @Override public boolean isTerminated() {
    return false;
  }

  @Override public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return false;
  }

  @Override public void execute(Runnable command) {
    command.run();
  }
}
