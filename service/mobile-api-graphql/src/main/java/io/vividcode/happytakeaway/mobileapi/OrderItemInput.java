package io.vividcode.happytakeaway.mobileapi;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Name;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Name("OrderItemInput")
public class OrderItemInput {

  private String itemId;
  private int quantity;
  private BigDecimal price;
}
