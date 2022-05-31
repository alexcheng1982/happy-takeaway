package io.vividcode.happytakeaway.order.grpc;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.vividcode.happytakeaway.common.grpc.StreamObserverHelper;
import io.vividcode.happytakeaway.order.api.v1.ConfirmOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.ConfirmOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersRequest;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersResponse;
import io.vividcode.happytakeaway.order.api.v1.GetOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.GetOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.MarkAsReadyForDeliveryRequest;
import io.vividcode.happytakeaway.order.api.v1.MarkAsReadyForDeliveryResponse;
import io.vividcode.happytakeaway.order.api.v1.OrderServiceGrpc.OrderServiceImplBase;
import io.vividcode.happytakeaway.order.service.OrderService;
import javax.inject.Inject;

@GrpcService
public class OrderGrpcService extends OrderServiceImplBase {

  @Inject OrderService orderService;

  @Override
  @Blocking
  public void createOrder(
      CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
    StreamObserverHelper.sendSingleValue(responseObserver, request, this.orderService::createOrder);
  }

  @Override
  @Blocking
  public StreamObserver<GetOrderRequest> getOrder(
      StreamObserver<GetOrderResponse> responseObserver) {
    return StreamObserverHelper.sendStream(responseObserver, this.orderService::getOrder);
  }

  @Override
  @Blocking
  public void findOrders(
      FindOrdersRequest request, StreamObserver<FindOrdersResponse> responseObserver) {
    StreamObserverHelper.sendSingleValue(responseObserver, request, this.orderService::findOrders);
  }

  @Override
  @Blocking
  public void confirmOrder(
      ConfirmOrderRequest request, StreamObserver<ConfirmOrderResponse> responseObserver) {
    StreamObserverHelper.sendSingleValue(
        responseObserver, request, this.orderService::confirmOrder);
  }

  @Override
  @Blocking
  public void markAsReadyForDelivery(
      MarkAsReadyForDeliveryRequest request,
      StreamObserver<MarkAsReadyForDeliveryResponse> responseObserver) {
    StreamObserverHelper.sendSingleValue(
        responseObserver, request, this.orderService::markAsReadyForDelivery);
  }
}
