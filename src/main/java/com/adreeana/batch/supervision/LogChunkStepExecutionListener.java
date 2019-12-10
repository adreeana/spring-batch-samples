package com.adreeana.batch.supervision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class LogChunkStepExecutionListener<T, S> implements StepExecutionListener,
  SkipListener<T, S> {

  private static final Logger log = LoggerFactory.getLogger(LogChunkStepExecutionListener.class);

  public LogChunkStepExecutionListener() {
  }

  @Override
  public ExitStatus afterStep(final StepExecution stepExecution) {
    log.info("[{}] after_step {}", stepExecution.getJobExecution().getJobInstance()
      .getJobName(), stepExecution.getSummary());
    return null;
  }

  @Override
  public void beforeStep(final StepExecution stepExecution) {
    log.info("[{}] before_step {}", stepExecution.getJobExecution().getJobInstance()
      .getJobName(), stepExecution.getSummary());
  }

  @Override
  public void onSkipInProcess(final T item, final Throwable t) {
    log.warn("{} [ {} ]", t.getMessage(), item);
  }

  @Override
  public void onSkipInRead(final Throwable t) {
    log.warn(t.getMessage());
  }

  @Override
  public void onSkipInWrite(final S item, final Throwable t) {
    log.warn("{} [ {} ]", t.getMessage(), item);
  }
}
