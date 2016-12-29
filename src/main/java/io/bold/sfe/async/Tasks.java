package io.bold.sfe.async;

import com.google.common.util.concurrent.ListenableFuture;

public final class Tasks {
  public static String getTaskName(Object data) {
    return getTaskName(data.getClass());
  }

  public static String getTaskName(Class<?> dataType) {
    return String.format("task-%s", dataType.getSimpleName());
  }

  public static ListenableFuture<Boolean> submitAsync(TaskProcessor processor, Object data) {
    return processor.submitAsync(getTaskName(data), data);
  }

  private Tasks() {}
}
