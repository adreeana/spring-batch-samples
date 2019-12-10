package com.adreeana.batch.validation;

import com.adreeana.batch.supervision.LogJobListener;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class ValidatedJobConfiguration {
  private static final Logger log = LoggerFactory.getLogger(ValidatedJobConfiguration.class);

  private static final FlowExecutionStatus ALL = new FlowExecutionStatus("ALL");
  private static final FlowExecutionStatus VALIDATION = new FlowExecutionStatus("VALIDATION");

  @Bean
  public Job validatedJob(JobBuilderFactory jobBuilders
    , LogJobListener logJobListener
    , JobParametersValidator validatedJobParametersValidator
    , JobExecutionDecider validationOnlyDecider
    , Step validateConfigurationStep
    , Step archiveStep) {

    Flow flow = new FlowBuilder<Flow>("flow")
      .start(validateConfigurationStep)
      .next(validationOnlyDecider)
      .on(ALL.toString())
      .to(archiveStep)
      .from(validationOnlyDecider)
      .on(VALIDATION.toString())
      .end()
      .build();

    return jobBuilders.get("validatedJob")
      .validator(validatedJobParametersValidator)
      .listener(logJobListener)
      .start(flow)
      .end()
      .build();
  }

  @Bean
  public JobParametersValidator validatedJobParametersValidator() {
    return parameters -> {
      log.info("Validating execution context [job parameters].");

      if (Strings.isEmpty(parameters.getString("fileName"))) {
        throw new JobParametersInvalidException("Missing 'fileName' parameter");
      }

      log.info("Execution context [job parameters] OK.");
    };
  }

  @Bean
  @JobScope
  public JobExecutionDecider validationOnlyDecider() {
    return (jobExecution, stepExecution) -> {
      final String validation = jobExecution.getJobParameters().getString("validation");

      if (Boolean.valueOf(validation)) {
        return new FlowExecutionStatus("VALIDATION");
      }
      return new FlowExecutionStatus("ALL");
    };
  }

  @Bean
  public Step validateConfigurationStep(StepBuilderFactory stepBuilders
    , @Value("${cft.archive.dir}") String archiveDir
  ) {
    return stepBuilders.get("validateConfigurationStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Validating execution context [job configuration].");
        validateDirectory(archiveDir);
        log.info("Execution context [job configuration] OK.");

        return RepeatStatus.FINISHED;
      })
      .allowStartIfComplete(Boolean.TRUE)
      .build();
  }

  @Bean
  public Step archiveStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("archiveStep")
      .tasklet((contribution, chunkContext) -> {
        return RepeatStatus.FINISHED;
      })
      .build();
  }

  private void validateDirectory(final String dir) throws JobExecutionException {
    if (Strings.isEmpty(dir)) {
      throw new JobExecutionException("Missing 'archiveDir' configuration entry.");
    }
    try {
      File file = new File(dir);

      if (!file.canRead()) {
        throw new JobExecutionException("Directory '" + dir + "' cannot be read.");
      }

    } catch (RuntimeException e) {
      throw new JobExecutionException("Directory '" + dir + "' does not exist.");
    }
  }
}