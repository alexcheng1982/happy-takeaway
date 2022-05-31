package io.vividcode.happytakeaway.delivery.client;

import io.grpc.ManagedChannelBuilder;
import io.smallrye.mutiny.Multi;
import io.vividcode.happytakeaway.delivery.api.v1.MutinyDeliveryServiceGrpc;
import io.vividcode.happytakeaway.delivery.api.v1.MutinyDeliveryServiceGrpc.MutinyDeliveryServiceStub;
import io.vividcode.happytakeaway.delivery.api.v1.UpdateRiderPositionRequest;
import io.vividcode.happytakeaway.delivery.api.v1.UpdateRiderPositionResponse;
import java.util.stream.Stream;

public class GrpcClient {

  public Stream<UpdateRiderPositionResponse> callService() {
    MutinyDeliveryServiceStub stub =
        MutinyDeliveryServiceGrpc.newMutinyStub(
            ManagedChannelBuilder.forAddress("localhost", 9001).usePlaintext().build());
    Multi<UpdateRiderPositionResponse> response =
        stub.updateRiderPosition(
            Multi.createFrom()
                .item(UpdateRiderPositionRequest.newBuilder().setRiderId("1").build()));
    return response.subscribe().asStream();
  }

  public static void main(String[] args) {
    Stream<UpdateRiderPositionResponse> results = new GrpcClient().callService();
    results.forEach(System.out::println);
  }
}
