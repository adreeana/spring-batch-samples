package com.adreeana.batch.retry;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MailingJobFunctionalTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  @Qualifier("mailingJob")
  private Job mailingJob;

  @Test
  public void testJob() {

    try {

      jobLauncherTestUtils.setJob(mailingJob);
      final JobExecution jobExecution = jobLauncherTestUtils.launchJob();

      assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}