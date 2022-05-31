package io.vividcode.happytakeaway.restaurant.api;

import io.vividcode.happytakeaway.common.base.PageRequest;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class GetMenuRequest {

  @NonNull private String restaurantId;
  @NonNull private String menuId;
  @NonNull private PageRequest pageRequest;
}
