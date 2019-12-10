package com.adreeana.batch.integration.auto_correct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.integration.annotation.Router;

public class JobExecutionAutoCorrectRouter {
  private static final Logger log = LoggerFactory.getLogger(JobExecutionAutoCorrectRouter.class);

  @Router(inputChannel = "jobExecutionStatusChannel")
  public String route(JobExecution payload) {
    String channel = "nullChannel";

    if (!Boolean.valueOf(payload.getJobParameters().getString("validation"))
      && payload.getStatus().isUnsuccessful()) {
      channel = "jobExecutionAutoCorrectChannel";

      log.info("[ROUTER] Routed to {} on batch execution status {}", channel, payload.getStatus());

    }
    return channel;
  }
}
