package io.vividcode.happytakeaway.delivery.api.v1.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryTask;
import io.vividcode.happytakeaway.event.api.GenericEvent;

@RegisterForReflection
public class DeliveryTaskCreatedEvent extends GenericEvent<DeliveryTask> {

  public static final String TYPE = "DeliveryTaskCreated";

  public DeliveryTaskCreatedEvent(DeliveryTask payload) {
    super(TYPE, payload);
  }

  public DeliveryTaskCreatedEvent(String eventId, long timestamp, DeliveryTask payload) {
    super(TYPE, eventId, timestamp, payload);
  }
}
