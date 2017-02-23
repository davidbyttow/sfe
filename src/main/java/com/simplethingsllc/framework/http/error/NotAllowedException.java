package com.simplethingsllc.framework.http.error;

public class NotAllowedException extends javax.ws.rs.NotAllowedException {
  public NotAllowedException() {
    super("Method not allowed");
  }
}
