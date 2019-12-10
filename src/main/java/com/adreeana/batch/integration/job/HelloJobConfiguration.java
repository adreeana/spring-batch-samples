package com.adreeana.batch.integration.job;

import com.adreeana.batch.supervision.LogJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class HelloJobConfiguration {

  private static final Logger log = LoggerFactory.getLogger(HelloJobConfiguration.class);

  @Bean("helloJob")
  public Job helloJob(JobBuilderFactory jobBuilders,
                      LogJobListener logJobListener,
                      JobExecutionListener jobExecutionStatusListener,
                      Step helloStep,
                      Step archiveStep) {
    return jobBuilders.get("helloJob")
      .listener(logJobListener)
      .listener(jobExecutionStatusListener)
      .start(helloStep)
      .next(archiveStep)
      .build();
  }

  @Bean
  @JobScope
  public Step helloStep(StepBuilderFactory stepBuilders,
                        @Value("#{jobParameters['helloFile']}") String fileName) {
    return stepBuilders.get("helloStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Hello with greetings {}", fileName);
        return RepeatStatus.FINISHED;
      })
      .build();
  }

  @Bean
  public Step archiveStep(StepBuilderFactory stepBuilders, @Value("${cft.archive.dir}") String
    archiveDir) {
    return stepBuilders.get("archiveStep")
      .tasklet((contribution, chunkContext) -> {
        FileSystemResource ressource = new FileSystemResource(archiveDir);
        if (!ressource.exists()) {
          log.info("Advisedly fail step in order to test automatic relaunch.");
          throw new ApplicationException(ApplicationErrorCodes.E0000001, "Archive dir does " +
            archiveDir + " not exist.");
        }
        return RepeatStatus.FINISHED;
      })
      .build();
  }

}
