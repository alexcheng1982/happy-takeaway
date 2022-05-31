package io.vividcode.happytakeaway.delivery.resource;

import io.smallrye.mutiny.Uni;
import io.vividcode.happytakeaway.delivery.entity.DeliveryTaskInfo;
import io.vividcode.happytakeaway.delivery.repository.DeliveryTaskRepository;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("deliveryTask")
public class DeliveryTaskResource {

  @Inject DeliveryTaskRepository deliveryTaskRepository;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Tag(ref = "DeliveryTask")
  public Uni<List<DeliveryTaskInfo>> listAll() {
    return this.deliveryTaskRepository.listTasks();
  }
}
