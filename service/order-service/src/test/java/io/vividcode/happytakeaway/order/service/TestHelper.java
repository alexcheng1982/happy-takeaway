package io.vividcode.happytakeaway.order.service;

import com.github.javafaker.Faker;
import io.vividcode.happytakeaway.order.api.v1.CreateOrderRequest;
import io.vividcode.happytakeaway.order.api.v1.OrderItem;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestHelper {

  private static final Faker faker = Faker.instance();

  private TestHelper() {}

  public static CreateOrderRequest createOrderRequest() {
    return createOrderRequest(uuid(), uuid(), 3);
  }

  public static CreateOrderRequest createOrderRequest(
      String userId, String restaurantId, int numberOfItems) {
    return CreateOrderRequest.newBuilder()
        .setUserId(userId)
        .setRestaurantId(restaurantId)
        .addAllItems(
            IntStream.range(0, numberOfItems)
                .mapToObj(i -> TestHelper.orderItem())
                .collect(Collectors.toList()))
        .build();
  }

  public static String uuid() {
    return UUID.randomUUID().toString();
  }

  private static OrderItem orderItem() {
    return OrderItem.newBuilder()
        .setItemId(uuid())
        .setQuantity(faker.random().nextInt(10))
        .setPrice(faker.number().randomDouble(6, 1, 1000))
        .build();
  }
}
