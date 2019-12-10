package com.adreeana.batch.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class SMTPMailService {
  private static final Logger log = LoggerFactory.getLogger(SMTPMailService.class);

  @Retryable(
      value = {MessageDeliveryException.class, Exception.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 5000))
  public void sendMail() throws Exception {
    log.info("Something bad happens when sending the mail.");

    throw new MessageDeliveryException("Throw a new MessageDeliveryException to see what happens");
  }

  @Recover
  public void recover(MessageDeliveryException e) {
    //implements recover behaviour
    log.info("if the sendMail() method throws a MessageDeliveryException, the recover() method is " +
        "called");
  }
}
