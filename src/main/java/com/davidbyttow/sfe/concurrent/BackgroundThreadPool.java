package com.davidbyttow.sfe.concurrent;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binding annotation for the background thread pool.  Background threads should be used for activity which
 * does not block an incoming requests.  Requests that want to parallelize their own activity should use
 * the {@link RequestThreadPool} which applies proper backpressure.
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface BackgroundThreadPool {}
