package io.vividcode.happytakeaway.restaurant.api.v1.web;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuItemWebResponse {

  private String id;
  private String name;
  private String description;
  private BigDecimal price;
}
