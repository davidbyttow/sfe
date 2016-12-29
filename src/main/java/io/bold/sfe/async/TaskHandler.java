package io.bold.sfe.async;

public interface TaskHandler<T> {
  void process(T data);
}
