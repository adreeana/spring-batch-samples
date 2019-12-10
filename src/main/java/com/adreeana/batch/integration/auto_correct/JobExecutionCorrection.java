package com.adreeana.batch.integration.auto_correct;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.util.Assert;

public class JobExecutionCorrection {

  private JobExecution jobExecution;

  private Boolean corrected;

  public JobExecutionCorrection(final JobExecution jobExecution) {
    Assert.notNull(jobExecution, "Job execution must not be null.");
    this.jobExecution = jobExecution;
    corrected = Boolean.FALSE;
  }

  public void corrected() {
    corrected = Boolean.TRUE;
  }

  public Boolean isCorrected() {
    return corrected;
  }

  public JobParameters jobParameters() {
    return jobExecution.getJobParameters();
  }
}
