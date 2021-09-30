package io.vividcode.happytakeaway.order.api.v1.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class OrderDetails {

  String orderId;
  String userId;
  String restaurantId;
  List<OrderItem> items;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderItem {

    String itemId;
    Integer quantity;
    BigDecimal price;
  }
}
