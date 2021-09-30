package io.vividcode.happytakeaway.restaurant.api.v1.web;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class UpdateMenuItemWebRequest {

  private String restaurantId;
  private String name;
  private String description;
  private BigDecimal price;
}
