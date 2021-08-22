package io.vividcode.happytakeaway.order.api.v1.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vividcode.happytakeaway.event.api.GenericEvent;

@RegisterForReflection
public class OrderCreatedEvent extends GenericEvent<OrderDetails> {

  public static final String TYPE = "OrderCreated";

  public OrderCreatedEvent(OrderDetails orderDetails) {
    super(TYPE, orderDetails);
  }

  public OrderCreatedEvent(String eventId, long timestamp, OrderDetails payload) {
    super(TYPE, eventId, timestamp, payload);
  }
}
