package io.vividcode.happytakeaway.delivery.event;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryTask;
import io.vividcode.happytakeaway.delivery.api.v1.event.DeliveryTaskCreatedEvent;
import io.vividcode.happytakeaway.event.consumer.BaseEventConsumer;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@UnlessBuildProfile("test")
public class DeliveryTaskEventConsumer extends BaseEventConsumer {

  @Inject
  @Channel("delivery-task-created")
  Emitter<DeliveryTask> emitter;

  @Override
  protected String getConsumerId() {
    return "order-delivery-task";
  }

  @Override
  protected List<String> getTopics() {
    return List.of("outbox.event.DeliveryTask");
  }

  void onStart(@Observes StartupEvent e) {
    this.addEventHandler(
        DeliveryTaskCreatedEvent.TYPE, DeliveryTaskCreatedEvent.class, this::onDeliveryTaskCreated);
    this.start();
  }

  public void onDeliveryTaskCreated(DeliveryTaskCreatedEvent event) {
    this.emitter.send(event.getPayload());
  }

  void onShutdown(@Observes ShutdownEvent e) {
    this.stop();
  }
}
