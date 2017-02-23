package com.simplethingsllc.framework.http.error;

public class ErrorDetails {

  public static String ERROR_BAD_REQUEST = "BAD_REQUEST";

  public static String ERROR_UNAUTHORIZED = "UNAUTHORIZED";

  public static String ERROR_INTERNAL = "INTERNAL";

  /** Redundantly matches the status code returned */
  int status = 500;

  /** A unique error name to identify the error */
  String type = ERROR_INTERNAL;

  /** A user readable message that can be presented to user (but not necessary) */
  String message = "There was a problem, try again.";

  /** Debug message for development */
  String detailMessage;
}
