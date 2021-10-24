package io.vividcode.happytakeaway.restaurant.api.v1;

import io.vividcode.happytakeaway.common.base.PagedResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantWithMenuItems {

  private String id;
  private String name;
  private String description;
  private Address address;
  private PagedResult<MenuItem> menuItems;
}
