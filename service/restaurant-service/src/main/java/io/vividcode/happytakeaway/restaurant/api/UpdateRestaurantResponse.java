package io.vividcode.happytakeaway.restaurant.api;

import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRestaurantResponse {

  private String id;
  private String name;
  private String description;
  private Address address;
}
