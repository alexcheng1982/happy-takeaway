package io.vividcode.happytakeaway.restaurant.api.v1.web;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuItemWebRequest {

  @NonNull private String name;
  private String coverImage;
  private String description;
  @NonNull private BigDecimal price;
}
