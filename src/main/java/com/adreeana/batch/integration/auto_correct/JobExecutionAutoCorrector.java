package com.adreeana.batch.integration.auto_correct;

import com.adreeana.batch.integration.job.ApplicationErrorCodes;
import com.adreeana.batch.integration.job.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.util.Assert;

import java.io.File;

public class JobExecutionAutoCorrector {
  private static final Logger log = LoggerFactory.getLogger(JobExecutionAutoCorrector.class);

  private final String archiveDir;

  public JobExecutionAutoCorrector(final String archiveDir) {
    this.archiveDir = archiveDir;
  }

  public JobExecutionCorrection autoCorrect(JobExecution jobExecution) {
    Assert.isTrue(jobExecution.getStatus().isUnsuccessful(), "Handles only failed jobs");

    final JobExecutionCorrection result = new JobExecutionCorrection(jobExecution);

    log.info("[AUTO CORRECT] Attempting to auto correct job execution failure ...");

      jobExecution.getStepExecutions().stream()
      .filter((se) -> se.getStatus().isUnsuccessful() && "archiveStep".equals(se.getStepName()))
      .forEach((se) -> {
        //correct step in error if possible
        se.getFailureExceptions().stream()
          .filter((e) -> e instanceof ApplicationException
            && ApplicationErrorCodes.E0000001.equals(((ApplicationException) e).getCode()))
          .forEach((e) -> {
              File directory = new File(String.valueOf(archiveDir));
              if (!directory.exists()) {
                directory.mkdir();
                result.corrected();
                log.info("{} [AUTO CORRECT] [ERROR ACQUITTAL] Auto correction done on failed step{}"
                  , ApplicationErrorCodes.E0000001.code()
                  , se.getStepName());
              }
            }
          );
      });

    return result;
  }
}
