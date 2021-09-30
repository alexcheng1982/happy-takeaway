package io.vividcode.happytakeaway.delivery.api.v1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryPickupInvitation {

  private String riderId;
  private DeliveryTask deliveryTask;
}
