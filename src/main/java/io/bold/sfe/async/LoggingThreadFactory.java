package io.bold.sfe.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LoggingThreadFactory implements ThreadFactory {

  private static final Logger log = LoggerFactory.getLogger(LoggingThreadFactory.class);

  private static final Thread.UncaughtExceptionHandler ERROR_HANDLER
      = (t, e) -> log.error(String.format("Uncaught exception in thread %s", t.getName()), e);

  private final AtomicInteger nextId = new AtomicInteger(0);

  @Override public Thread newThread(Runnable r) {
    Thread thread = new Thread(r);
    thread.setName(String.format("background-%d", nextId.incrementAndGet()));
    thread.setContextClassLoader(getClass().getClassLoader());
    thread.setUncaughtExceptionHandler(ERROR_HANDLER);
    return thread;
  }
}
