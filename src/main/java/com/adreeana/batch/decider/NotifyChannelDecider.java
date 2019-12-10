package com.adreeana.batch.decider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class NotifyChannelDecider implements JobExecutionDecider {
  private static final Logger log = LoggerFactory.getLogger(NotifyChannelDecider.class);

  static final FlowExecutionStatus MAIL = new FlowExecutionStatus("MAIL");
  static final FlowExecutionStatus SMS = new FlowExecutionStatus("SMS");
  static final FlowExecutionStatus NONE = new FlowExecutionStatus("NONE");

  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    final Long randomLong = stepExecution.getJobParameters().getLong("randomLong");
    Assert.notNull(randomLong, "randomInt not found in job execution context.");
    
    log.info("Executing Decision with randomInt = " + randomLong);

    if (randomLong % 2 == 0) {
      return MAIL;
    }
    if (randomLong % 3 == 0) {
      return SMS;
    }
    return NONE;
  }
}