package io.vividcode.happytakeaway.event.consumer;

public interface DuplicateMessageDetector {

  boolean isDuplicate(String consumerId, String messageId);
}
