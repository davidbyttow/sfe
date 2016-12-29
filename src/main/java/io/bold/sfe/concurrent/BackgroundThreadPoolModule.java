package io.bold.sfe.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Provides;
import io.bold.sfe.async.LoggingThreadFactory;
import io.bold.sfe.common.ProviderOnlyModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Installs a pre-tuned background thread pool, for random in-process asynchronous tasks / deferreds */
public final class BackgroundThreadPoolModule extends ProviderOnlyModule {
  private static final Logger log = LoggerFactory.getLogger(LoggingThreadFactory.class);

  private static final Thread.UncaughtExceptionHandler ERROR_HANDLER
      = (t, e) -> log.error(String.format("Uncaught exception in thread %s", t.getName()), e);

  @Provides
  @BackgroundThreadPool
  @Singleton ExecutorService backgroundExecutor() {
    return Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setDaemon(true)
        .setNameFormat("background-%d")
        .setUncaughtExceptionHandler(ERROR_HANDLER)
        .build());
  }

  @Provides
  @BackgroundThreadPool
  @Singleton ListeningExecutorService backgroundListeningExecutor(@BackgroundThreadPool ExecutorService executor) {
    return MoreExecutors.listeningDecorator(executor);
  }

  @Provides
  @RequestThreadPool
  @Singleton
  ExecutorService requestExecutor() {
    // TODO(matt): Figure out appropriate threading, queueing, and backpressure strategy
    return Executors.newCachedThreadPool(new ThreadFactoryBuilder()
        .setDaemon(true)
        .setNameFormat("request-parallel-%d")
        .setUncaughtExceptionHandler(ERROR_HANDLER)
        .build());
  }

  @Provides
  @RequestThreadPool
  @Singleton
  ListeningExecutorService requestListeningExecutor(@RequestThreadPool ExecutorService executor) {
    return MoreExecutors.listeningDecorator(executor);
  }
}
