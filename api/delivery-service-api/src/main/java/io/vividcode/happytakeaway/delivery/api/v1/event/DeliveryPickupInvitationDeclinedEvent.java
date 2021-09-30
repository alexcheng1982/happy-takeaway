package io.vividcode.happytakeaway.delivery.api.v1.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPickupInvitationDeclinedEvent {

  private String riderId;
  private String deliveryTaskId;
}
