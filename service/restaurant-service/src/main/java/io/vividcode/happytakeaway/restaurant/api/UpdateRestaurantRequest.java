package io.vividcode.happytakeaway.restaurant.api;

import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class UpdateRestaurantRequest {

  @NonNull private String id;
  private String name;
  private String description;
  private Address address;
}
