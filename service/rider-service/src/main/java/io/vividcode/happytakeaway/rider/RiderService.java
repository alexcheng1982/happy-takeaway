package io.vividcode.happytakeaway.rider;

import io.vividcode.happytakeaway.delivery.api.v1.DeliveryPickupInvitation;
import io.vividcode.happytakeaway.delivery.api.v1.event.DeliveryPickupInvitationAcceptedEvent;
import io.vividcode.happytakeaway.delivery.api.v1.event.DeliveryPickupInvitationDeclinedEvent;
import io.vividcode.happytakeaway.rider.api.v1.RiderDeliveryRequest;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class RiderService {

  @Incoming("delivery-pickup-invitation")
  void onDeliveryPickupInvitation(DeliveryPickupInvitation invitation) {
    System.out.println(invitation);
  }

  @Incoming("delivery-pickup-accept")
  @Outgoing("delivery-pickup-invitation-accepted")
  DeliveryPickupInvitationAcceptedEvent acceptDeliveryPickupInvitation(
      RiderDeliveryRequest request) {
    return DeliveryPickupInvitationAcceptedEvent.builder()
        .deliveryTaskId(request.getDeliveryTaskId())
        .riderId(request.getRiderId())
        .build();
  }

  @Incoming("delivery-pickup-decline")
  @Outgoing("delivery-pickup-invitation-declined")
  DeliveryPickupInvitationDeclinedEvent declineDeliveryPickupInvitation(
      RiderDeliveryRequest request) {
    return DeliveryPickupInvitationDeclinedEvent.builder()
        .deliveryTaskId(request.getDeliveryTaskId())
        .riderId(request.getRiderId())
        .build();
  }
}
