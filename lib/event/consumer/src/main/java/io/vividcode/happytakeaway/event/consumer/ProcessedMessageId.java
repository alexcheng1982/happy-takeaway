package io.vividcode.happytakeaway.event.consumer;

import java.io.Serializable;
import lombok.Data;

@Data
public class ProcessedMessageId implements Serializable {

  private static final long serialVersionUID = 1L;
  private String consumerId;
  private String messageId;
}
