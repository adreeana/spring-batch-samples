package com.adreeana.batch.partitions;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * End-To-End Testing of Batch Person Job
 * <p>
 * This is a functional Test : we provide input values and verify the output values without concern
 * for any implementation details.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonJobFunctionalTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private Job personJob;

  @Test
  public void testJob() {

    try {
      final JobExecution jobExecution = executeJob("without_skips/", "persons*.csv");
      assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

      final Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
      stepExecutions.forEach((s) -> {
        if (s.getStepName().startsWith("importPersonStep")) {
          assertEquals(ExitStatus.COMPLETED, s.getExitStatus());
          assertEquals(5, s.getReadCount());
          assertEquals(5, s.getWriteCount());
        }
      });

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testJobWithSkips() {

    try {
      final JobExecution jobExecution = executeJob("with_skips/", "persons*.csv");
      assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

      final Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
      stepExecutions.forEach((s) -> {
        if (s.getStepName().startsWith("importPersonStep")) {
          assertEquals(ExitStatus.COMPLETED, s.getExitStatus());
          assertEquals(3, s.getReadCount());
          assertEquals(3, s.getWriteCount());
          assertEquals(1, s.getSkipCount());
        }
      });

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  private JobExecution executeJob(final String directory, final String filePattern) throws Exception {
    jobLauncherTestUtils.setJob(personJob);

    return jobLauncherTestUtils.launchJob(new
      JobParametersBuilder()
      .addString("directoryPath", directory)
      .addString("filePattern", filePattern)
      .toJobParameters
        ());
  }
}