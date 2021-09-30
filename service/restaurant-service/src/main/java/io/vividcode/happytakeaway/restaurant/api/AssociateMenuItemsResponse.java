package io.vividcode.happytakeaway.restaurant.api;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociateMenuItemsResponse {

  private String menuId;
  private Set<String> menuItems;
}
