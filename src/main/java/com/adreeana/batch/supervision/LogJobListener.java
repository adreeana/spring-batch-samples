package com.adreeana.batch.supervision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Provides callback after the completion of a job. The callback is called after both both
 * successful and failed executions.
 */
@Component
public final class LogJobListener implements JobExecutionListener {
  private static final Logger log = LoggerFactory.getLogger(LogJobListener.class);

  @Override
  public void beforeJob(final JobExecution jobExecution) {
    log.info("[{}] before_job {}", jobExecution.getJobInstance().getJobName(), jobExecution.toString());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (ExitStatus.FAILED.equals(jobExecution.getExitStatus())) {
      log.error("[{}] after_job {} {}", jobExecution.getJobInstance().getJobName(), jobExecution
        .toString(), stepExecutions
        (jobExecution));
    } else {
      log.info("[{}] after_job {} {}", jobExecution.getJobInstance().getJobName(), jobExecution
        .toString(), stepExecutions
        (jobExecution));
    }
  }

  private static String stepExecutions(final JobExecution jobExecution) {
    return String.format(
      "stepExecutions=[%s]", jobExecution.getStepExecutions()
        .stream()
        .map(se -> String.format("[%s, startTime=%s, endTime=%s]", se.getSummary(), se
            .getStartTime(),
          se.getEndTime()))
        .collect(Collectors.joining(",")));
  }
}
