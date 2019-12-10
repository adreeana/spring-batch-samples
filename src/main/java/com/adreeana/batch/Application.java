package com.adreeana.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.UUID;

/**
 * Application class is the main class responsible for launching the batch application. The
 * application starts one job, the job has its own Configuration class PersonJobConfiguration.
 */
@EnableRetry
@SpringBootApplication(scanBasePackages = "com.adreeana*")
@EnableTransactionManagement
@EnableScheduling
public class Application {
  private static final Logger log = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) throws Exception {
    final ConfigurableApplicationContext ctx =  SpringApplication.run(Application.class, args);
    final JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);

    try {
      jobLauncher.run((Job)ctx.getBean("validatedJob"), new JobParametersBuilder()
        .addString("lot", UUID.randomUUID().toString())
        .addString("validation", Boolean.toString(Boolean.TRUE))
        .addString("fileName", "some file")
        .toJobParameters());

    } catch (JobExecutionAlreadyRunningException |
      JobRestartException |
      JobInstanceAlreadyCompleteException |
      JobParametersInvalidException e) {
      log.error(e.getMessage());
    }
  }
}
