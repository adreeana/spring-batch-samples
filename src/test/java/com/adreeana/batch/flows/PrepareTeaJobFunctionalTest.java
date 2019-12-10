package com.adreeana.batch.flows;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * End-To-End Testing of Batch Person Job
 * <p>
 * This is a functional Test : we provide input values and verify the output values without concern
 * for any implementation details.
 */
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PrepareTeaJobFunctionalTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  @Qualifier("prepareTeaJob")
  private Job prepareTeaJob;

  @Test
  public void testJob() {

    try {
      jobLauncherTestUtils.setJob(prepareTeaJob);
      final JobExecution jobExecution = jobLauncherTestUtils.launchJob();

      assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}