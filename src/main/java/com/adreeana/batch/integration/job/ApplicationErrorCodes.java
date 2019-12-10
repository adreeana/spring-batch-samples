package com.adreeana.batch.integration.job;

public enum ApplicationErrorCodes {
  E0000001("E0000001")

  ,
  E0000002("E0000002")
  ;

  ApplicationErrorCodes(final String code) {
    this.code = code;
  }

  public String code() {
    return this.code;
  }

  private String code;
}
