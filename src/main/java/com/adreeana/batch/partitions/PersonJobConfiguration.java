package com.adreeana.batch.partitions;

import com.adreeana.batch.supervision.LogChunkStepExecutionListener;
import com.adreeana.batch.supervision.LogJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;

import javax.sql.DataSource;
import java.io.FileNotFoundException;

@Configuration
public class PersonJobConfiguration {

  private static final Logger log = LoggerFactory.getLogger(PersonJobConfiguration.class);

  @Primary
  @Bean
  public Job personJob(JobBuilderFactory jobBuilders,
                       LogJobListener logJobListener,
                       Step masterImportPersonStep) {
    return jobBuilders.get("personJob")
      .incrementer(new RunIdIncrementer())
      .listener(logJobListener)
      .start(masterImportPersonStep)
      .build();
  }

  @Bean
  public Step masterImportPersonStep(StepBuilderFactory stepBuilders,
                                     Step importPersonStep,
                                     MultiResourcePartitioner personPartitioner) {
    return stepBuilders.get("masterImportPersonStep")
      .partitioner(importPersonStep.getName(), personPartitioner)
      .partitionHandler(partitionHandler(null)).build();
  }

  @Bean
  public Step importPersonStep(StepBuilderFactory stepBuilders, TaskExecutor taskExecutor) {
    return stepBuilders.get("importPersonStep")
      .<Person, Person>chunk(10)
      .faultTolerant()
      .noSkip(FileNotFoundException.class)
      .skip(FlatFileParseException.class)
      .skipLimit(3)
      .retryLimit(3)
      .backOffPolicy(backOffPolicy())
      .retry(DeadlockLoserDataAccessException.class)
      .reader(personReader(null))
      .processor(personProcessor())
      .writer(personWriter(null))
      .taskExecutor(taskExecutor)
      .listener(logChunkStepExecutionListener())
      .build();
  }

  @Bean
  public BackOffPolicy backOffPolicy() {
    final FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(5000L);
    return backOffPolicy;
  }

  @Bean
  @StepScope
  public MultiResourcePartitioner personPartitioner(
    @Value("#{jobParameters['directoryPath']}") String directoryPath,
    @Value("#{jobParameters['filePattern']}") String filePattern) {
    final MultiResourcePartitioner multiResourcePartitioner = new MultiResourcePartitioner();
    multiResourcePartitioner.setResources(Resources.getResources(directoryPath + filePattern));
    return multiResourcePartitioner;
  }

  @Bean
  public PartitionHandler partitionHandler(TaskExecutor partitionTaskExecutor) {
    final TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
    handler.setGridSize(20);
    handler.setTaskExecutor(partitionTaskExecutor);
    handler.setStep(importPersonStep(null, null));
    return handler;
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Person> personReader(
    @Value("#{stepExecutionContext['fileName']}") String fileURL) {

    return new FlatFileItemReaderBuilder<Person>()
      .name("personItemReader")
      .linesToSkip(1)
      .resource(Resources.getResource(fileURL))
      .delimited()
      .names(new String[]{"firstName", "lastName"})
      .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
        setTargetType(Person.class);
      }})
      .build();
  }

  @Bean
  public ItemProcessor<Person, Person> personProcessor() {
    return (item -> {
      final Person transformedItem = new Person(item.getFirstName(),
        item.getLastName().toUpperCase());
      log.info("Converting {} into {}", item, transformedItem);
      return transformedItem;
    });
  }

  @Bean
  public ItemWriter<Person> personWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Person>()
      .dataSource(dataSource)
      .sql("INSERT INTO Person(first_name, last_name) VALUES (:firstName, :lastName)")
      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider())
      .build();
  }

  @Bean
  public StepExecutionListener logChunkStepExecutionListener() {
    return new LogChunkStepExecutionListener<Person, Person>();
  }
}