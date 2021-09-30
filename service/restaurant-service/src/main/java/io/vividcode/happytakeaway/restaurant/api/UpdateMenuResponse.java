package io.vividcode.happytakeaway.restaurant.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateMenuResponse {

  private String id;
  private String name;
}
