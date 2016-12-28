package com.davidbyttow.sfe.concurrent;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Binding annotation for executors that support parallel activity in request threads.  Use this thread pool
 * when you have a request thread that wants to do work in parallel, and block until all of that work is complete.
 * The {@link RequestThreadPool} has different queueing and backpressure policies than {@link BackgroundThreadPool},
 * making it more suitable for request blocking work.
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestThreadPool {
}
