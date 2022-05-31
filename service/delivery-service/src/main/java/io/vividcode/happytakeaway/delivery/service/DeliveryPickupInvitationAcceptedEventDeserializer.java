package io.vividcode.happytakeaway.delivery.service;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.vividcode.happytakeaway.delivery.api.v1.event.DeliveryPickupInvitationAcceptedEvent;

public class DeliveryPickupInvitationAcceptedEventDeserializer
    extends ObjectMapperDeserializer<DeliveryPickupInvitationAcceptedEvent> {

  public DeliveryPickupInvitationAcceptedEventDeserializer() {
    super(DeliveryPickupInvitationAcceptedEvent.class);
  }
}
