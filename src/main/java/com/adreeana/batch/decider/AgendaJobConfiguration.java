package com.adreeana.batch.decider;

import com.adreeana.batch.supervision.LogJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

@Configuration
public class AgendaJobConfiguration {
  private static final Logger log = LoggerFactory.getLogger(AgendaJobConfiguration.class);

  @Bean
  public Job agendaJob(JobBuilderFactory jobBuilders, LogJobListener logJobListener) {
    return jobBuilders.get("agendaJob")
      .validator(jobParametersValidator())
      .listener(logJobListener)
      .preventRestart()
      .start(notifyFlow(null))
      .end()
      .build();
  }

  @Bean
  public JobParametersValidator jobParametersValidator() {
    return parameters -> {
      final Long randomLong = parameters.getLong("randomLong");
      if (randomLong == null) {
        throw new JobParametersInvalidException("Missing randomLong parameter");
      }
    };
  }

  @Bean
  public Flow notifyFlow(JobExecutionDecider notifyChannelDecider) {
    return new FlowBuilder<Flow>("randomFlow")
      .start(notifyChannelDecider).on("MAIL").to(notifyByMailStep(null))
      .from(notifyChannelDecider).on("SMS").to(notifyBySMSStep(null))
      .from(notifyChannelDecider).on("NONE").end("COMPLETED")
      .build();
  }

  @Bean
  public Step notifyByMailStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("notifyByMailStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Notify by Mail");
        return FINISHED;
      })
      .build();
  }

  @Bean
  public Step notifyBySMSStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("notifyBySMSStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Notify by SMS");
        return FINISHED;
      })
      .build();
  }
}