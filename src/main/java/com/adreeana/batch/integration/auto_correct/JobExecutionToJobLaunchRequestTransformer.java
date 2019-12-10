package com.adreeana.batch.integration.auto_correct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.util.Assert;

public class JobExecutionToJobLaunchRequestTransformer {
  private static final Logger log = LoggerFactory.getLogger(JobExecutionToJobLaunchRequestTransformer.class);

  private final Job job;

  public JobExecutionToJobLaunchRequestTransformer(Job job) {
    this.job = job;
  }

  @Transformer
  public JobLaunchRequest transform(JobExecutionCorrection jobExecutionCorrection) {
    Assert.notNull(jobExecutionCorrection.isCorrected(), "Job execution must be corrected.");

    final JobLaunchRequest result = new JobLaunchRequest(job, jobExecutionCorrection.jobParameters());

    log.info("[TRANSFORMER] Transformed failed job execution in job lunching request {}.", result);

    return result;
  }
}
