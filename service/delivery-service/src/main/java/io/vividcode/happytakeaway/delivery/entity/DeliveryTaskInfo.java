package io.vividcode.happytakeaway.delivery.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryTaskInfo {

  private String id;
  private String status;
  private String riderId;
}
