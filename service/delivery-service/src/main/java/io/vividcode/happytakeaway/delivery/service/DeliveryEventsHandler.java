package io.vividcode.happytakeaway.delivery.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Merge;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryPickupInvitation;
import io.vividcode.happytakeaway.delivery.api.v1.DeliveryTask;
import io.vividcode.happytakeaway.delivery.api.v1.event.DeliveryPickupInvitationAcceptedEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DeliveryEventsHandler {

  private static final Logger LOGGER = Logger.getLogger(DeliveryEventsHandler.class);

  @Inject DeliveryService deliveryService;

  @Incoming("delivery-task-created")
  @Outgoing("delivery-rider-search")
  public Uni<DeliveryRiderSearch> onDeliveryTask(DeliveryTask deliveryTask) {
    LOGGER.infov("New delivery task: {0}", deliveryTask);
    return this.deliveryService.taskCreated(deliveryTask);
  }

  @Incoming("delivery-rider-search")
  @Outgoing("delivery-pickup-invitation")
  @Merge
  public Multi<DeliveryPickupInvitation> onSearch(DeliveryRiderSearch search) {
    LOGGER.infov("Search for riders: {0}", search);
    this.deliveryService.checkForRidersAcceptance(search);
    return this.deliveryService.searchForRiders(search);
  }

  @Incoming("delivery-pickup-invitation-accepted")
  public Uni<Void> onPickupInvitationAccepted(DeliveryPickupInvitationAcceptedEvent event) {
    LOGGER.infov("Pickup invitation accepted: {0}", event);
    return this.deliveryService.acceptDeliveryPickupInvitation(
        event.getDeliveryTaskId(), event.getRiderId());
  }
}
