package io.vividcode.happytakeaway.delivery.resource;

import io.quarkus.vertx.web.ReactiveRoutes;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.Route.HttpMethod;
import io.smallrye.mutiny.Multi;
import io.vividcode.happytakeaway.delivery.entity.DeliveryTaskInfo;
import io.vividcode.happytakeaway.delivery.repository.DeliveryTaskRepository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(title = "Delivery Task API", version = "1.0.0"),
    tags = @Tag(name = "DeliveryTask", description = "Delivery task"))
@ApplicationScoped
public class DeliveryTaskRoutes {

  @Inject DeliveryTaskRepository deliveryTaskRepository;

  @Route(path = "/deliveryTasks", methods = HttpMethod.GET)
  @Tag(ref = "DeliveryTask")
  Multi<DeliveryTaskInfo> deliveryTasks() {
    return ReactiveRoutes.asEventStream(
        this.deliveryTaskRepository
            .listTasks()
            .toMulti()
            .flatMap(list -> Multi.createFrom().iterable(list)));
  }
}
