package com.adreeana.batch.integration.auto_correct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Router;

public class JobExecutionRelaunchRouter {
  private static final Logger log = LoggerFactory.getLogger(JobExecutionRelaunchRouter.class);

  @Router(inputChannel = "jobExecutionAutoCorrectStatusChannel")
  public String route(JobExecutionCorrection payload) {
    String channel = "nullChannel";
    if (payload.isCorrected()) {
      channel = "jobExecutionRelaunchChannel";
    }

    log.info("[ROUTER] Routed to {} on auto correction status {}", channel, payload.isCorrected() ?
      "OK" : "KO");

    return channel;
  }
}
