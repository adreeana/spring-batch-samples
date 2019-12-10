package com.adreeana.batch.partitions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the reader using the TestExecutionListener.
 * <p>
 * This is an integration test : we test the reader in realistic conditions to validate overall
 * functionality (we use a real Spring and Spring Batch contexts, including the real batch job
 * definition)
 */
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
  StepScopeTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonReaderIntegrationTest {
  @Autowired
  private FlatFileItemReader<Person> personReader;

  /**
   * Create custom step execution. The factory method you create in the test case (getStepExecution)
   * is called before each test method to obtain a new StepExecution.
   */
  public StepExecution getStepExecution() {
    final StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    stepExecution.getExecutionContext().put("fileName",
      Resources.getURL("without_skips/persons_itg.csv"));
    return stepExecution;
  }

  @Test
  public void testPersonReader() {
    personReader.open(getStepExecution().getExecutionContext());
    try {
      int readCount = 0;
      while (personReader.read() != null) {
        readCount++;
      }
      assertEquals(5, readCount);

    } catch (Exception e) {
      fail(e.getMessage());

    } finally {
      personReader.close();
    }
  }
}
