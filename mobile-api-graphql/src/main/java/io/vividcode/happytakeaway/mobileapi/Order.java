package io.vividcode.happytakeaway.mobileapi;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Name;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Name("Order")
public class Order {

  private String orderId;
  private String userId;
  private String restaurantId;
  private String status;
  private List<OrderItem> items;
}
