package com.adreeana.batch.integration.launch_on_event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class FileToJobLaunchRequestTransformer {
  private static final Logger log = LoggerFactory.getLogger(FileToJobLaunchRequestTransformer.class);

  private final String fileParameter;
  private final Job job;

  public FileToJobLaunchRequestTransformer(String fileParameter, Job job) {
    this.fileParameter = fileParameter;
    this.job = job;
  }

  @Transformer
  public JobLaunchRequest transform(File file) {
    final JobLaunchRequest result = new JobLaunchRequest(job,
      new JobParametersBuilder()
        .addLong("lot", LocalDateTime.now().getLong(ChronoField.MILLI_OF_DAY))
        .addString(fileParameter, file.getAbsolutePath())
        .toJobParameters());

    log.info("[TRANSFORMER] Transformed file {} in job launch request {}.", file, result);

    return result;
  }
}
