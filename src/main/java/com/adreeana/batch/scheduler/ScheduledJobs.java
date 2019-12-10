package com.adreeana.batch.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Random;

@Component
@Profile("dev,prod")
public class ScheduledJobs {
  private static final Logger log = LoggerFactory.getLogger(ScheduledJobs.class);

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier("agendaJob")
  private Job agendaJob;

  @Scheduled(cron = "0 0 * * * *")
  public void scheduleAgendaJob() {
    try {
      log.info("Job execution is scheduled with Spring scheduler");
      jobLauncher.run(agendaJob,
        new JobParametersBuilder().addLong("exec_id",
          LocalDateTime.now().getLong(ChronoField.MILLI_OF_DAY)).addLong("randomLong", new Random()
          .nextLong())
          .toJobParameters());

    } catch (JobExecutionAlreadyRunningException |
      JobRestartException |
      JobInstanceAlreadyCompleteException |
      JobParametersInvalidException e) {
      log.error(e.getMessage());
    }
  }
}
