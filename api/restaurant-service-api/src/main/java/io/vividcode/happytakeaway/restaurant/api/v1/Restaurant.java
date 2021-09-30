package io.vividcode.happytakeaway.restaurant.api.v1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Restaurant {

  private String id;
  private String name;
  private String description;
  private Address address;
  private String activeMenuId;
}
