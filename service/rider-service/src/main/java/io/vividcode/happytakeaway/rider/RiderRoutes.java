package io.vividcode.happytakeaway.rider;

import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.smallrye.mutiny.Uni;
import io.vividcode.happytakeaway.rider.api.v1.RiderDeliveryRequest;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@OpenAPIDefinition(
    info = @Info(title = "Rider API", version = "1.0.0"),
    tags = @Tag(name = "Rider", description = "Rider"))
@ApplicationScoped
public class RiderRoutes {

  @Inject
  @Channel("delivery-pickup-accept")
  Emitter<RiderDeliveryRequest> acceptEmitter;

  @Inject
  @Channel("delivery-pickup-decline")
  Emitter<RiderDeliveryRequest> declineEmitter;

  @Route(path = "/delivery/accept", methods = HttpMethod.POST)
  @Tag(ref = "Rider")
  Uni<Void> acceptDeliveryPickup(@Body RiderDeliveryRequest request) {
    return Uni.createFrom().completionStage(this.acceptEmitter.send(request));
  }

  @Route(path = "/delivery/decline", methods = HttpMethod.POST)
  @Tag(ref = "Rider")
  Uni<Void> declineDeliveryPickup(@Body RiderDeliveryRequest request) {
    return Uni.createFrom().completionStage(this.declineEmitter.send(request));
  }
}
