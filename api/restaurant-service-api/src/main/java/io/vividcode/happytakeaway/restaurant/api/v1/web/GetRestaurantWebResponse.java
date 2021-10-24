package io.vividcode.happytakeaway.restaurant.api.v1.web;

import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRestaurantWebResponse {

  private String id;
  private String name;
  private String description;
  private Address address;
  private PagedResult<MenuItem> menuItems;
}
