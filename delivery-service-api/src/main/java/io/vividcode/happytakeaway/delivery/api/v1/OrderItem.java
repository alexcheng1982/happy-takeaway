package io.vividcode.happytakeaway.delivery.api.v1;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

  private String itemId;
  private Integer quantity;
  private BigDecimal price;
}
