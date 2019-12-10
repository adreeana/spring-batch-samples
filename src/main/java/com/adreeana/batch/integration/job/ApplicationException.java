package com.adreeana.batch.integration.job;

public class ApplicationException extends Exception {
  private ApplicationErrorCodes code;

  public ApplicationException(final ApplicationErrorCodes code, final String message) {
    super(message);
    this.code = code;
  }

  public ApplicationException(final ApplicationErrorCodes code, final String message, final Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public ApplicationErrorCodes getCode() {
    return code;
  }
}
