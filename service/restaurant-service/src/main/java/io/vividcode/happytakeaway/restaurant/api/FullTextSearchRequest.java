package io.vividcode.happytakeaway.restaurant.api;

import io.vividcode.happytakeaway.common.base.PageRequest;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FullTextSearchRequest {

  @NonNull private String query;

  private BigDecimal minPrice;

  private BigDecimal maxPrice;

  @NonNull private PageRequest pageRequest;
}
