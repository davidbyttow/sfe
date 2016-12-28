package com.davidbyttow.sfe.async;

import com.davidbyttow.sfe.common.Times;
import com.davidbyttow.sfe.json.Json;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import org.joda.time.DateTime;
import org.joda.time.ReadableDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class TaskProcessor implements Managed {

  private static final Logger log = LoggerFactory.getLogger(TaskProcessor.class);

  @Inject public TaskProcessor(AsyncRunner asyncRunner) {
    this.asyncRunner = asyncRunner;
  }

  private static class PendingTask {
    String taskName;
    Object data;
    DateTime eta = null;

    @Override public String toString() {
      return String.format("Task=%s request=%s", taskName, Json.writeValueAsString(data));
    }
  }

  private static class RegisteredTaskHandler {
    TaskHandler<?> taskHandler;
    Method processMethod;
  }

  private final LinkedBlockingQueue<PendingTask> queue = new LinkedBlockingQueue<>();
  private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
  private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(128);
  private final List<ScheduledExecutorService> repeatedSchedulers = Collections.synchronizedList(new ArrayList<>());
  private final Map<String, RegisteredTaskHandler> registeredTaskHandlers = new HashMap<>();
  private final AsyncRunner asyncRunner;
  private Thread workerThread;

  @Override public void start() throws Exception {
    workerThread = new Thread(() -> {
      while (!shuttingDown.get()) {
        try {
          pullOne();
        } catch (InterruptedException e) {
          try {
            Thread.sleep(10);
          } catch (InterruptedException ignored) {
            // do nothing
          }
        } catch (Throwable t) {
          log.error("Error processing task", t);
        }
      }
    });
    workerThread.start();
  }

  @Override public void stop() throws Exception {
    shuttingDown.set(true);
    scheduledExecutor.shutdown();
    for (ScheduledExecutorService executorService : repeatedSchedulers) {
      executorService.shutdown();
    }
    workerThread.interrupt();
    workerThread.join();
  }

  public void registerTaskHandler(String name, TaskHandler<?> handler) {
    if (registeredTaskHandlers.containsKey(name)) {
      throw new IllegalArgumentException("Task handler already exists for name " + name);
    }
    log.info("Registered task " + name);
    RegisteredTaskHandler registered = new RegisteredTaskHandler();
    registered.taskHandler = handler;

    try {
      registered.processMethod = handler.getClass().getMethod("process", Object.class);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
    registeredTaskHandlers.put(name, registered);
  }

  public ListenableFuture<Boolean> submitAsync(String taskName, Object data) {
    return scheduleAsync(taskName, data, null);
  }

  public ListenableFuture<Boolean> scheduleAsync(String taskName, Object data, @Nullable ReadableDuration delay) {
    return asyncRunner.runAsync(() -> schedule(taskName, data, delay));
  }

  public void scheduleAtFixedRate(String name, Object data, ReadableDuration period, @Nullable ReadableDuration initialDelay) {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    repeatedSchedulers.add(scheduler);

    scheduler.scheduleAtFixedRate(() -> {
      try {
        schedule(name, data, null);
      } catch (Throwable t) {
        log.error("Error processing taskHandler", t);
        throw Throwables.propagate(t);
      }
    }, initialDelay == null ? 0 : initialDelay.getMillis(), period.getMillis(), TimeUnit.MILLISECONDS);
  }

  private boolean schedule(String taskName, Object data, @Nullable ReadableDuration delay) {
    if (shuttingDown.get()) {
      throw new IllegalStateException("Couldn't accept taskHandler during shutdown");
    }

    try {
      PendingTask pendingTask = newPendingTask(taskName, data);
      if (delay != null) {
        pendingTask.eta = Times.nowUtc().plus(delay);
      }

      if (!queue.offer(pendingTask, 1, TimeUnit.SECONDS)) {
        log.error("Dropped task {}", pendingTask);
        return false;
      }
      return true;
    } catch (InterruptedException e) {
      throw Throwables.propagate(e);
    }
  }

  private PendingTask newPendingTask(String taskName, Object data) {
    if (!registeredTaskHandlers.containsKey(taskName)) {
      throw new IllegalArgumentException("No handler found for task: " + taskName);
    }

    PendingTask pendingTask = new PendingTask();
    pendingTask.taskName = taskName;
    pendingTask.data = data;
    return pendingTask;
  }

  private void pullOne() throws InterruptedException {
    PendingTask task = queue.take();
    if (task.eta != null) {
      DateTime now = Times.nowUtc();
      long delay = task.eta.getMillis() - now.getMillis();
      if (delay > 0) {
        scheduledExecutor.schedule(newTaskRunner(task), delay, TimeUnit.MILLISECONDS);
      } else {
        scheduledExecutor.submit(newTaskRunner(task));
      }
    } else {
      scheduledExecutor.submit(newTaskRunner(task));
    }
  }

  private Runnable newTaskRunner(PendingTask task) {
    RegisteredTaskHandler registeredTaskHandler = registeredTaskHandlers.get(task.taskName);
    if (registeredTaskHandler == null) {
      throw new IllegalStateException("No task handler found for type: " + task.taskName);
    }
    return () -> {
      try {
        registeredTaskHandler.processMethod.invoke(registeredTaskHandler.taskHandler, task.data);
      } catch (Exception e) {
        log.error("Error processing task={}", task.taskName, e);
        throw Throwables.propagate(e);
      }
    };
  }
}
