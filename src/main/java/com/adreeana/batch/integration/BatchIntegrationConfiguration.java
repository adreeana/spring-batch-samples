package com.adreeana.batch.integration;

import com.adreeana.batch.integration.auto_correct.JobExecutionAutoCorrectRouter;
import com.adreeana.batch.integration.auto_correct.JobExecutionAutoCorrector;
import com.adreeana.batch.integration.auto_correct.JobExecutionRelaunchRouter;
import com.adreeana.batch.integration.auto_correct.JobExecutionToJobLaunchRequestTransformer;
import com.adreeana.batch.integration.launch_on_event.FileToJobLaunchRequestTransformer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;

/**
 * java @IntegrationComponentScan The Spring Integration analogue of @ComponentScan to scan
 * components based on interfaces, (the Spring Framework’s @ComponentScan only looks at classes).
 * Spring Integration supports the discovery of interfaces annotated with @MessagingGateway (see #7
 * below).
 */
@Configuration
@EnableIntegration
@EnableAsync
@IntegrationComponentScan
public class BatchIntegrationConfiguration {
  @Bean
  public DirectChannel jobExecutionsChannel() {
    return new DirectChannel();
  }

  @Bean
  public DirectChannel jobExecutionStatusChannel() {
    return new DirectChannel();
  }

  @Bean
  public DirectChannel jobExecutionAutoCorrectChannel() {
    return new DirectChannel();
  }

  @Bean
  public DirectChannel jobExecutionAutoCorrectStatusChannel() {
    return new DirectChannel();
  }

  @Bean
  public DirectChannel jobExecutionRelaunchChannel() {
    return new DirectChannel();
  }

  @Bean
  public JobExecutionAutoCorrectRouter jobExecutionAutoCorrectRouter() {
    return new JobExecutionAutoCorrectRouter();
  }

  @Bean
  public JobExecutionRelaunchRouter jobExecutionRelaunchRouter() {
    return new JobExecutionRelaunchRouter();
  }

  @Bean
  public FileToJobLaunchRequestTransformer fileToJobLaunchRequestTransformer(
    @Qualifier("helloJob") Job helloJob) {
    return new FileToJobLaunchRequestTransformer("helloFile", helloJob);
  }

  @Bean
  public JobExecutionToJobLaunchRequestTransformer jobExecutionToJobLaunchRequestTransformer(
    @Qualifier("helloJob") Job helloJob) {
    return new JobExecutionToJobLaunchRequestTransformer(helloJob);
  }

  @Bean
  @ServiceActivator(inputChannel = "jobExecutionAutoCorrectChannel",
    outputChannel = "jobExecutionAutoCorrectStatusChannel")
  public JobExecutionAutoCorrector jobExecutionAutoCorrect(
    @Value("${cft.archive.dir}") String archiveDir) {
    return new JobExecutionAutoCorrector(archiveDir);
  }

  @Bean
  public IntegrationFlow launchFlow(@Value("${cft.dir}") String cftReceiveDir) {
    return IntegrationFlows
      .from(Files.inboundAdapter(
        new File(cftReceiveDir)).preventDuplicates(true)
          .filter(new SimplePatternFileListFilter("persons*.csv"))
              , c -> c.poller(Pollers.fixedRate(5000).maxMessagesPerPoll(1)))
      .handle(fileToJobLaunchRequestTransformer(null))
      .handle(jobLaunchingGateway(null))
      .log(LoggingHandler.Level.INFO, "headers.id + ': ' + payload")
      .get();
  }

  @Bean
  public IntegrationFlow relaunchFlow() {
    return IntegrationFlows.from("jobExecutionRelaunchChannel")
      .handle(jobExecutionToJobLaunchRequestTransformer(null))
      .handle(jobRelaunchingGateway(null))
      .log(LoggingHandler.Level.INFO, "headers.id + ': ' + payload")
      .get();
  }

  @Bean
  public JobLaunchingGateway jobLaunchingGateway(JobLauncher jobLauncher) {
    return getJobLaunchingGateway(jobLauncher);
  }

  @Bean
  public JobLaunchingGateway jobRelaunchingGateway(JobLauncher jobLauncher) {
    return getJobLaunchingGateway(jobLauncher);
  }

  private JobLaunchingGateway getJobLaunchingGateway(final JobLauncher jobLauncher) {
    final JobLaunchingGateway gateway = new JobLaunchingGateway(jobLauncher);
    gateway.setOutputChannel(jobExecutionsChannel());
    return gateway;
  }

  @MessagingGateway(name = "jobExecutionStatusListener", defaultRequestChannel =
    "nullChannel", defaultReplyChannel = "jobExecutionsChannel")
  public interface JobExecutionStatusListener extends JobExecutionListener {
    @Override
    @Async
    @Gateway(requestChannel = "jobExecutionStatusChannel")
    void afterJob(@Payload JobExecution jobExecution);
  }
}