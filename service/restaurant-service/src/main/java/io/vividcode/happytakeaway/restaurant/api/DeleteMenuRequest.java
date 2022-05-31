package io.vividcode.happytakeaway.restaurant.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class DeleteMenuRequest {

  @NonNull private String restaurantId;
  @NonNull private String id;
}
