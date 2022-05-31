package io.vividcode.happytakeaway.mobileapi;

import io.quarkus.grpc.GrpcClient;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersRequest;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersRequest.Builder;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersResponse;
import io.vividcode.happytakeaway.order.api.v1.OrderItem;
import io.vividcode.happytakeaway.order.api.v1.OrderServiceGrpc.OrderServiceBlockingStub;
import io.vividcode.happytakeaway.order.api.v1.PageRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FullTextSearchResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FullTextSearchWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.GetRestaurantWebResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@GraphQLApi
public class MobileApi {

  @Inject @RestClient RestaurantServiceClient restaurantServiceClient;

  @Inject
  @GrpcClient("order-service")
  OrderServiceBlockingStub orderServiceBlockingStub;

  @Query("searchRestaurants")
  @Description("Search restaurants")
  public FullTextSearchResponse searchRestaurants(
      @Name("request") FullTextSearchWebRequest request) {
    return this.restaurantServiceClient.search(request);
  }

  @Mutation("createOrder")
  @Description("Create order")
  public CreateOrderResult createOrder(@Name("order") CreateOrderInput input) {
    return this.createOrderResult(
        this.orderServiceBlockingStub.createOrder(this.createOrderRequest(input)));
  }

  @Query("findOrders")
  @Description("Find orders")
  public List<Order> findOrders(@Name("query") FindOrdersInput input) {
    return this.getOrders(this.orderServiceBlockingStub.findOrders(this.findOrdersRequest(input)));
  }

  @Name("restaurant")
  public GetRestaurantWebResponse getRestaurant(@Source Order order) {
    return this.restaurantServiceClient.get(order.getRestaurantId());
  }

  private CreateOrderResult createOrderResult(CreateOrderResponse response) {
    return CreateOrderResult.builder().orderId(response.getOrderId()).build();
  }

  private FindOrdersRequest findOrdersRequest(FindOrdersInput input) {
    Builder builder = FindOrdersRequest.newBuilder();
    if (input.getUserId() != null) {
      builder.setUserId(input.getUserId());
    }
    if (input.getRestaurantId() != null) {
      builder.setRestaurantId(input.getRestaurantId());
    }
    if (input.getStatus() != null) {
      builder.setStatus(input.getStatus());
    }
    if (input.getPageRequest() != null) {
      builder.setPageRequest(
          PageRequest.newBuilder()
              .setPage(input.getPageRequest().getPage())
              .setSize(input.getPageRequest().getSize())
              .build());
    } else {
      builder.setPageRequest(PageRequest.newBuilder().setPage(0).setSize(10).build());
    }
    return builder.build();
  }

  private CreateOrderRequest createOrderRequest(CreateOrderInput input) {
    return CreateOrderRequest.newBuilder()
        .setUserId(input.getUserId())
        .setRestaurantId(input.getRestaurantId())
        .addAllItems(
            input.getItems().stream()
                .map(
                    item ->
                        OrderItem.newBuilder()
                            .setItemId(item.getItemId())
                            .setQuantity(item.getQuantity())
                            .setPrice(item.getPrice().doubleValue())
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  private List<Order> getOrders(FindOrdersResponse response) {
    return response.getResult().getOrdersList().stream()
        .map(
            order ->
                Order.builder()
                    .userId(order.getUserId())
                    .orderId(order.getOrderId())
                    .restaurantId(order.getRestaurantId())
                    .status(order.getStatus())
                    .items(
                        order.getItemsList().stream()
                            .map(
                                item ->
                                    io.vividcode.happytakeaway.mobileapi.OrderItem.builder()
                                        .itemId(item.getItemId())
                                        .quantity(item.getQuantity())
                                        .price(BigDecimal.valueOf(item.getPrice()))
                                        .build())
                            .collect(Collectors.toList()))
                    .build())
        .collect(Collectors.toList());
  }
}
