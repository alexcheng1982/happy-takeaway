package io.vividcode.happytakeaway.delivery.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vividcode.happytakeaway.delivery.api.v1.DisableRiderRequest;
import io.vividcode.happytakeaway.delivery.api.v1.DisableRiderResponse;
import io.vividcode.happytakeaway.delivery.api.v1.MutinyDeliveryServiceGrpc;
import io.vividcode.happytakeaway.delivery.api.v1.UpdateRiderPositionRequest;
import io.vividcode.happytakeaway.delivery.api.v1.UpdateRiderPositionResponse;
import io.vividcode.happytakeaway.delivery.service.DeliveryService;
import javax.inject.Inject;

@GrpcService
public class DeliveryGrpcService extends MutinyDeliveryServiceGrpc.DeliveryServiceImplBase {

  @Inject DeliveryService deliveryService;

  @Override
  public Multi<UpdateRiderPositionResponse> updateRiderPosition(
      Multi<UpdateRiderPositionRequest> request) {
    return this.deliveryService.updateRiderPosition(request);
  }

  @Override
  public Uni<DisableRiderResponse> disableRider(DisableRiderRequest request) {
    return this.deliveryService.disableRider(request);
  }
}
