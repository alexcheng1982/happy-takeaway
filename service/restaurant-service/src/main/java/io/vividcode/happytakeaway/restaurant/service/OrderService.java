package io.vividcode.happytakeaway.restaurant.service;

import io.quarkus.grpc.GrpcClient;
import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.order.api.v1.ConfirmOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersRequest;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersRequest.Builder;
import io.vividcode.happytakeaway.order.api.v1.MarkAsReadyForDeliveryRequest;
import io.vividcode.happytakeaway.order.api.v1.OrderServiceGrpc.OrderServiceBlockingStub;
import io.vividcode.happytakeaway.order.api.v1.OrdersResult;
import io.vividcode.happytakeaway.order.api.v1.PageRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.Order;
import io.vividcode.happytakeaway.restaurant.api.v1.Order.OrderItem;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FindOrdersResponse;
import io.vividcode.happytakeaway.restaurant.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.opentracing.Traced;

@ApplicationScoped
public class OrderService {

  @Inject RestaurantRepository restaurantRepository;

  @Inject OwnerIdProvider ownerIdProvider;

  @Inject
  @GrpcClient("order-service")
  OrderServiceBlockingStub orderServiceBlockingStub;

  @Traced
  public FindOrdersResponse listOrders(
      String restaurantId, String orderStatus, PageRequest pageRequest) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), restaurantId)
        .map(
            restaurant -> {
              Builder builder =
                  FindOrdersRequest.newBuilder()
                      .setRestaurantId(restaurant.getId())
                      .setPageRequest(pageRequest);
              if (orderStatus != null) {
                builder.setStatus(orderStatus);
              }
              var response = this.orderServiceBlockingStub.findOrders(builder.build());
              OrdersResult result = response.getResult();
              return FindOrdersResponse.builder()
                  .results(
                      PagedResult.<Order>builder()
                          .currentPage(result.getCurrentPage())
                          .totalItems(result.getTotalItems())
                          .totalPages(result.getTotalPages())
                          .data(
                              result.getOrdersList().stream()
                                  .map(
                                      order ->
                                          Order.builder()
                                              .orderId(order.getOrderId())
                                              .status(order.getStatus())
                                              .items(
                                                  order.getItemsList().stream()
                                                      .map(
                                                          item ->
                                                              OrderItem.builder()
                                                                  .itemId(item.getItemId())
                                                                  .quantity(item.getQuantity())
                                                                  .price(
                                                                      BigDecimal.valueOf(
                                                                          item.getPrice()))
                                                                  .build())
                                                      .collect(Collectors.toList()))
                                              .build())
                                  .collect(Collectors.toList()))
                          .build())
                  .build();
            })
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(restaurantId));
  }

  @Traced
  public boolean confirmOrder(String restaurantId, String orderId) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), restaurantId)
        .map(
            restaurant ->
                this.orderServiceBlockingStub
                    .confirmOrder(ConfirmOrderRequest.newBuilder().setOrderId(orderId).build())
                    .getResult())
        .orElse(false);
  }

  @Traced
  public boolean markAsReadyForDelivery(String restaurantId, String orderId) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), restaurantId)
        .map(
            restaurant ->
                this.orderServiceBlockingStub
                    .markAsReadyForDelivery(
                        MarkAsReadyForDeliveryRequest.newBuilder().setOrderId(orderId).build())
                    .getResult())
        .orElse(false);
  }
}
