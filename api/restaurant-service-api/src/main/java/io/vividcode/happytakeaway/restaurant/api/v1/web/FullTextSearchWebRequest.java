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
public class FullTextSearchWebRequest {

  @NonNull private String query;

  private BigDecimal minPrice;

  private BigDecimal maxPrice;

  private int page;

  private int size;
}
