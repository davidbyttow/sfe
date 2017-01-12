package com.simplethingsllc.store.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HandleWith {
  Class<? extends EntityHandler> value();
}
