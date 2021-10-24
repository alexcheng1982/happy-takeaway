package io.vividcode.happytakeaway.restaurant.api.v1.web;

import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.restaurant.api.v1.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullTextSearchResponse {

  private PagedResult<Restaurant> result;
}
