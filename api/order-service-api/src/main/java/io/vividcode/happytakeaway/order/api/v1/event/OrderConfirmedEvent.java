package io.vividcode.happytakeaway.order.api.v1.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vividcode.happytakeaway.event.api.GenericEvent;

@RegisterForReflection
public class OrderConfirmedEvent extends GenericEvent<String> {

  public static final String TYPE = "OrderConfirmed";

  public OrderConfirmedEvent(String payload) {
    super(TYPE, payload);
  }

  public OrderConfirmedEvent(String eventId, long timestamp, String payload) {
    super(TYPE, eventId, timestamp, payload);
  }
}
