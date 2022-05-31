package io.vividcode.happytakeaway.restaurant.event;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vividcode.happytakeaway.event.consumer.BaseEventConsumer;
import io.vividcode.happytakeaway.order.api.v1.event.OrderCreatedEvent;
import io.vividcode.happytakeaway.restaurant.ws.OrderEventsSocket;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class OrderEventConsumer extends BaseEventConsumer {

  @Inject OrderEventsSocket orderEventsSocket;

  @Override
  protected String getConsumerId() {
    return "restaurant-order";
  }

  @Override
  protected List<String> getTopics() {
    return List.of("outbox.event.Order");
  }

  void onStart(@Observes StartupEvent e) {
    this.addEventHandler(OrderCreatedEvent.TYPE, OrderCreatedEvent.class, this::onOrderCreated);
    this.start();
  }

  void onOrderCreated(OrderCreatedEvent event) {
    this.orderEventsSocket.sendEvent(event.getPayload().getRestaurantId(), event);
  }

  void onShutdown(@Observes ShutdownEvent e) {
    this.stop();
  }
}
