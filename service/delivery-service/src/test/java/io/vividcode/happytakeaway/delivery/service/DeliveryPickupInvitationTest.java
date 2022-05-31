package io.vividcode.happytakeaway.delivery.service;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource;
import io.vividcode.happytakeaway.common.test.RedisResource;
import io.vividcode.happytakeaway.delivery.api.v1.Address;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryPickupInvitation;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryTask;
import io.vividcode.happytakeaway.delivery.api.v1.event.DeliveryPickupInvitationAcceptedEvent;
import io.vividcode.happytakeaway.delivery.entity.DeliveryPickupInvitationStatus;
import io.vividcode.happytakeaway.delivery.entity.DeliveryTaskInfo;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import org.awaitility.Awaitility;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(RedisResource.class)
@QuarkusTestResource(PostgreSQLResource.class)
@QuarkusTestResource(InMemoryConnectorResource.class)
@DisplayName("Delivery pickup invitation")
public class DeliveryPickupInvitationTest {

  @Inject
  @Channel("delivery-task-created")
  Emitter<DeliveryTask> emitter;

  @Inject DeliveryService deliveryService;

  @Inject @Any InMemoryConnector connector;

  @Test
  void testDeliveryPickup() {
    String riderId = this.uuid();
    this.deliveryService
        .updateRiderPosition(
            Multi.createFrom()
                .items(
                    TestHelper.updateRiderPositionRequest(riderId, 0.00001, 0.00001),
                    TestHelper.updateRiderPositionRequest(this.uuid(), 0.00002, 0.00002)))
        .collect()
        .asList()
        .await()
        .indefinitely();
    InMemorySink<DeliveryPickupInvitation> invitation =
        this.connector.sink("delivery-pickup-invitation");
    InMemorySource<DeliveryPickupInvitationAcceptedEvent> event =
        this.connector.source("delivery-pickup-invitation-accepted");
    String taskId = this.uuid();
    this.emitter
        .send(
            DeliveryTask.builder()
                .id(taskId)
                .restaurantAddress(Address.builder().lng(0).lat(0).build())
                .build())
        .toCompletableFuture()
        .join();
    Awaitility.await()
        .pollInterval(Duration.ofSeconds(2))
        .atMost(Duration.ofSeconds(10))
        .until(() -> invitation.received().size() == 2);
    event.send(
        DeliveryPickupInvitationAcceptedEvent.builder()
            .deliveryTaskId(taskId)
            .riderId(riderId)
            .build());
    Awaitility.await()
        .pollInterval(Duration.ofSeconds(10))
        .atMost(Duration.ofSeconds(30))
        .until(
            () -> {
              DeliveryTaskInfo taskInfo =
                  this.deliveryService.getTask(taskId).await().indefinitely();
              return taskInfo != null
                  && Objects.equals(
                      taskInfo.getStatus(), DeliveryPickupInvitationStatus.SELECTED.name());
            });
  }

  private String uuid() {
    return UUID.randomUUID().toString();
  }
}
