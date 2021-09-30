package io.vividcode.happytakeaway.restaurant.api.v1;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuItem {

  private String id;
  private String name;
  private String coverImage;
  private String description;
  private BigDecimal price;
}
