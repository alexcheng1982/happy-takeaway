package io.vividcode.happytakeaway.restaurant.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class SetActiveMenuRequest {

  @NonNull private String restaurantId;
  @NonNull private String menuId;
}
