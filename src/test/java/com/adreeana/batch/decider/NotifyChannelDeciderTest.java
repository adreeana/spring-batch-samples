package com.adreeana.batch.decider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;


/**
 * Unit testing NotifyChannelDecider
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class NotifyChannelDeciderTest {
  private JobExecution jobExecution;

  private NotifyChannelDecider decider;

  @Before
  public void setUp() {
    jobExecution = MetaDataInstanceFactory.createJobExecution();
    decider = new NotifyChannelDecider();
  }

  @Test
  public void testDecideMail() {
    final StepExecution stepExecution = getStepExecution(4L);
    final FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);
    assertEquals(NotifyChannelDecider.MAIL, status);
  }

  @Test
  public void testDecideSms() {
    final StepExecution stepExecution = getStepExecution(9L);
    final FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);
    assertEquals(NotifyChannelDecider.SMS, status);
  }

  @Test
  public void testDecideNone() {
    final StepExecution stepExecution = getStepExecution(49L);
    final FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);
    assertEquals(NotifyChannelDecider.NONE, status);
  }

  private static StepExecution getStepExecution(final long randomLong) {
    return MetaDataInstanceFactory.createStepExecution(new JobParametersBuilder().addLong
      ("randomLong", randomLong).toJobParameters());
  }
}
