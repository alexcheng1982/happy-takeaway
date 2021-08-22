package io.vividcode.happytakeaway.event.consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class DatabaseDuplicateMessageDetector implements DuplicateMessageDetector {

  @Override
  @Transactional
  public boolean isDuplicate(String consumerId, String messageId) {
    try {
      new ProcessedMessageEntity(consumerId, messageId, System.currentTimeMillis())
          .persistAndFlush();
      return false;
    } catch (Exception e) {
      return true;
    }
  }
}
