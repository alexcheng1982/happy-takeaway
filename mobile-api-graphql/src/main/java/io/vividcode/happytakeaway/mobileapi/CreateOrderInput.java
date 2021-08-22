package io.vividcode.happytakeaway.mobileapi;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Name;

@Data
@NoArgsConstructor
@Name("CreateOrderInput")
public class CreateOrderInput {

  private String userId;
  private String restaurantId;
  private List<OrderItemInput> items;
}
