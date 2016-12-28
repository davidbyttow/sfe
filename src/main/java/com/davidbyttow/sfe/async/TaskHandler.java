package com.davidbyttow.sfe.async;

public interface TaskHandler<T> {
  void process(T data);
}
