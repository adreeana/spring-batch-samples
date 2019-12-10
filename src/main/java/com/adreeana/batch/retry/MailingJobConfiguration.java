package com.adreeana.batch.retry;

import com.adreeana.batch.supervision.LogJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

@Configuration
public class MailingJobConfiguration {
  private static final Logger log = LoggerFactory.getLogger(MailingJobConfiguration.class);

  @Bean
  public Job mailingJob(JobBuilderFactory jobBuilders,
                       LogJobListener logJobListener,
                       Step sendMailStep) {
    return jobBuilders.get("mailingJob")
      .listener(logJobListener)
      .preventRestart()
      .start(sendMailStep)
      .build();
  }

  @Bean
  public Step sendMailStep(StepBuilderFactory stepBuilders,
                           SMTPMailService smtpMailService) {
    return stepBuilders.get("sendMailStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Sending Mail ...");
        smtpMailService.sendMail();
        log.info("Mail sent.");
        return FINISHED;
      })
      .build();
  }
}