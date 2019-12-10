package com.adreeana.batch.supervision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class LogStepExecutionListener implements StepExecutionListener {
  private static final Logger log = LoggerFactory.getLogger(LogStepExecutionListener.class);

  @Override
  public void beforeStep(final StepExecution stepExecution) {
    log.info("[{}] before_step {}", stepExecution.getJobExecution().getJobInstance()
      .getJobName(), stepExecution.getSummary());
  }

  @Override
  public ExitStatus afterStep(final StepExecution stepExecution) {
    log.info("[{}] after_step {}", stepExecution.getJobExecution().getJobInstance()
      .getJobName(), stepExecution.getSummary());
    return null;
  }
}