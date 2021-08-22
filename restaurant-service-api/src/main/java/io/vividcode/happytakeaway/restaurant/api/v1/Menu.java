package io.vividcode.happytakeaway.restaurant.api.v1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Menu {

  private String id;
  private String name;
}
