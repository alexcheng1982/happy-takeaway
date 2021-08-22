package io.vividcode.happytakeaway.restaurant.api.v1;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {

  String orderId;
  String status;
  List<OrderItem> items;

  @Data
  @Builder
  public static class OrderItem {

    String itemId;
    Integer quantity;
    BigDecimal price;
  }
}
