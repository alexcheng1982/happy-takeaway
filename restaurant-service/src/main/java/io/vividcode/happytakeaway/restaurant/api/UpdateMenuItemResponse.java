package io.vividcode.happytakeaway.restaurant.api;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateMenuItemResponse {

  private String id;
  private String name;
  private String description;
  private BigDecimal price;
}
