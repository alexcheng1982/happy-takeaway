package io.vividcode.happytakeaway.rider.api.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiderDeliveryRequest {

  private String riderId;
  private String deliveryTaskId;
}
