package io.vividcode.happytakeaway.restaurant.api;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class CreateMenuItemRequest {

  @NonNull private String restaurantId;
  @NonNull private String name;
  private String coverImage;
  private String description;
  @NonNull private BigDecimal price;
}
