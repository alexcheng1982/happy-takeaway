package io.vividcode.happytakeaway.restaurant.api.v1.web;

import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import lombok.Data;

@Data
public class UpdateRestaurantWebRequest {

  private String name;
  private String description;
  private Address address;
}
