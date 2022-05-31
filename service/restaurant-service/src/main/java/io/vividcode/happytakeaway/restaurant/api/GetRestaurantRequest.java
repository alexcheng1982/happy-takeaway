package io.vividcode.happytakeaway.restaurant.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class GetRestaurantRequest {

  @NonNull private String id;
  private Integer numberOfMenuItems;
}
