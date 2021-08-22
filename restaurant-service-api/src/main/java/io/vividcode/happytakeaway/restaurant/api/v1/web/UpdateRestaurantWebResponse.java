package io.vividcode.happytakeaway.restaurant.api.v1.web;

import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantWebResponse {

  private String id;
  private String name;
  private String description;
  private Address address;
}
