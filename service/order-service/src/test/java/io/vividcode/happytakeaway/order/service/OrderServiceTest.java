package io.vividcode.happytakeaway.order.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderResponse;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersRequest;
import io.vividcode.happytakeaway.order.api.v1.FindOrdersResponse;
import javax.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("order service")
class OrderServiceTest {

  @Inject OrderService orderService;

  @Test
  @DisplayName("create order")
  void createOrder() {
    CreateOrderResponse response = this.orderService.createOrder(TestHelper.createOrderRequest());
    assertThat(response.getOrderId()).isNotNull();
  }

  @Test
  @DisplayName("find orders")
  void findOrders() {
    String userId = TestHelper.uuid();
    int numberOfMatchingOrders = 3;
    for (int i = 0; i < numberOfMatchingOrders; i++) {
      this.orderService.createOrder(TestHelper.createOrderRequest(userId, TestHelper.uuid(), 3));
    }
    int numberOfNonMatchingOrders = 2;
    for (int i = 0; i < numberOfNonMatchingOrders; i++) {
      this.orderService.createOrder(TestHelper.createOrderRequest());
    }
    FindOrdersResponse response =
        this.orderService.findOrders(FindOrdersRequest.newBuilder().setUserId(userId).build());
    assertThat(response.getResult().getTotalItems()).isEqualTo(numberOfMatchingOrders);
  }
}
