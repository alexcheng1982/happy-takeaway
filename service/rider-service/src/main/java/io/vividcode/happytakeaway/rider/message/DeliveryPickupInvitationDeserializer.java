package io.vividcode.happytakeaway.rider.message;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryPickupInvitation;

public class DeliveryPickupInvitationDeserializer
    extends ObjectMapperDeserializer<DeliveryPickupInvitation> {

  public DeliveryPickupInvitationDeserializer() {
    super(DeliveryPickupInvitation.class);
  }
}
