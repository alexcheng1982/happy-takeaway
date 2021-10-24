package io.vividcode.happytakeaway.restaurant.api.v1;

import io.vividcode.happytakeaway.common.base.PagedResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuWithItems {

  private String id;
  private String name;
  private PagedResult<MenuItem> items;
}
