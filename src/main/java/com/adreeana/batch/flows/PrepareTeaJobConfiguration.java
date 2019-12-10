package com.adreeana.batch.flows;

import com.adreeana.batch.supervision.LogJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

/**
 * Describes Prepare Tea Job using the Spring Batch configuration DSL
 */
@Configuration
public class PrepareTeaJobConfiguration {
  private static final Logger log = LoggerFactory.getLogger(PrepareTeaJobConfiguration.class);

  @Autowired
  private StepExecutionListener logStepExecutionListener;

  @Bean
  public Job prepareTeaJob(JobBuilderFactory jobBuilders,
                           TaskExecutor taskExecutor,
                           LogJobListener logJobListener) {
    Flow boilWaterFlow = new FlowBuilder<Flow>("boilWaterFlow")
      .from(boilWaterStep(null)).end();

    Flow addTeaFlow = new FlowBuilder<Flow>("addTeaFlow")
      .from(addTeaStep(null)).end();

    Flow addSugarFlow = new FlowBuilder<Flow>("addSugarFlow")
      .from(addSugarStep(null)).end();

    Flow addIngredientsFlow = new FlowBuilder<Flow>("addIngredientsFlow")
      .split(taskExecutor)
      .add(addTeaFlow, addSugarFlow).build();
        
    return jobBuilders.get("prepareTeaJob")
      .incrementer(new RunIdIncrementer())
      .listener(logJobListener)
      .start(boilWaterFlow)
      .next(addIngredientsFlow).next(addWaterStep(null))
      .end().build();

  }

  @Bean
  public Step addTeaStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("addTeaStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Adding tea bag for my tea");
        return FINISHED;
      })
      .listener(logStepExecutionListener)
      .build();
  }

  @Bean
  public Step addSugarStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("addSugarStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Adding sugar for my tea");
        return FINISHED;
      })
      .listener(logStepExecutionListener)
      .build();
  }

  @Bean
  public Step boilWaterStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("boilWaterStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Boiling water for my tea");
        return FINISHED;
      })
      .listener(logStepExecutionListener)
      .build();
  }

  @Bean
  public Step addWaterStep(StepBuilderFactory stepBuilders) {
    return stepBuilders.get("addWaterStep")
      .tasklet((contribution, chunkContext) -> {
        log.info("Adding water for my tea");
        return FINISHED;
      })
      .listener(logStepExecutionListener)
      .build();
  }
}