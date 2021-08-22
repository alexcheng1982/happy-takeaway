package io.vividcode.happytakeaway.restaurant.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetActiveMenuResponse {

  private String restaurantId;
  private String menuId;
}
