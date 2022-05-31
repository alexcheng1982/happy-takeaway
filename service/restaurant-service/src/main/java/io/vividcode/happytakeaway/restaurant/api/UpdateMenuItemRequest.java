package io.vividcode.happytakeaway.restaurant.api;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class UpdateMenuItemRequest {

  @NonNull private String restaurantId;
  @NonNull private String id;
  private String name;
  private String description;
  private BigDecimal price;
}
