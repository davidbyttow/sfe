package io.bold.sfe.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.bold.sfe.inject.ConfiguredGuiceBundle;
import io.bold.sfe.inject.GuiceBootstrap;
import io.bold.sfe.config.BasicServiceConfig;
import io.dropwizard.lifecycle.ExecutorServiceManager;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

public class BackgroundThreadPoolBundle<T extends BasicServiceConfig> implements ConfiguredGuiceBundle<T> {
  public static final Key<ListeningExecutorService> BACKGROUND_THREAD_POOL_KEY
      = Key.get(ListeningExecutorService.class, BackgroundThreadPool.class);
  public static final Key<ListeningExecutorService> REQUEST_THREAD_POOL_KEY
      = Key.get(ListeningExecutorService.class, RequestThreadPool.class);
  public static final Duration SHUTDOWN_DURATION = Duration.seconds(0);

  @Override public void initialize(GuiceBootstrap<?> bootstrap) {
    bootstrap.addModule(new BackgroundThreadPoolModule());
  }

  @Override public void run(Injector injector, T configuration, Environment environment) throws Exception {
    // TODO(matt): Allow configuration of thread pool shutdown, size, type, etc
    ListeningExecutorService backgroundExecutor = injector.getInstance(BACKGROUND_THREAD_POOL_KEY);
    environment.lifecycle().manage(new ExecutorServiceManager(backgroundExecutor, SHUTDOWN_DURATION, "background-pool"));

    ListeningExecutorService requestExecutor = injector.getInstance(REQUEST_THREAD_POOL_KEY);
    environment.lifecycle().manage(new ExecutorServiceManager(requestExecutor, SHUTDOWN_DURATION, "request-thread-pool"));
  }
}
