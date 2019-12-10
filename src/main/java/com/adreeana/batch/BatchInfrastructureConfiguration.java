package com.adreeana.batch;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * The BatchInfrastructureConfiguration class is responsible for configuring Spring Batch model
 * specific beans, such as JobRepository, JobExplorer, etc. It is loaded from Application, and it is
 * used by all jobs.
 * <p>
 * The @EnableBatchProcessing works similarly to the other @Enable* annotations in the Spring
 * family. In this case, @EnableBatchProcessing provides a base configuration for building batch
 * jobs. Within this base configuration, an instance of StepScope is created in addition to a number
 * of beans made available to be autowired: JobRepository - bean name "jobRepository" JobLauncher -
 * bean name "jobLauncher" JobRegistry - bean name "jobRegistry" PlatformTransactionManager - bean
 * name "transactionManager" JobBuilderFactory - bean name "jobBuilders" StepBuilderFactory - bean
 * name "stepBuilders"
 */
@Configuration
@EnableBatchProcessing
public class BatchInfrastructureConfiguration {
  @Bean
  public TaskExecutor taskExecutor() {
    final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(10);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setAllowCoreThreadTimeOut(true);
    return taskExecutor;
  }

  @Bean
  public TaskExecutor partitionTaskExecutor() {
    final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    //will have 5 active partitions at a time
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setMaxPoolSize(5);
    taskExecutor.setAllowCoreThreadTimeOut(true);
    return taskExecutor;
  }

  @Bean(destroyMethod = "close")
  public HikariDataSource dataSource(
    @Value("${spring.datasource.driver-class-name}") String driverClassName,
    @Value("${spring.datasource.url}") String datasourceUrl,
    @Value("${spring.datasource.username}") String username,
    @Value("${spring.datasource.password}") String password) {

    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setPoolName("HikariCP");

    hikariConfig.setMaximumPoolSize(15);
    hikariConfig.setMaxLifetime(MINUTES.toMillis(5));

    hikariConfig.setDriverClassName(driverClassName);
    hikariConfig.setJdbcUrl(datasourceUrl);
    hikariConfig.setUsername(username);
    hikariConfig.setPassword(password);

    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    HikariDataSource dataSource = new HikariDataSource(hikariConfig);

    return dataSource;
  }
}