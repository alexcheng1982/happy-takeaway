package io.vividcode.happytakeaway.restaurant.api;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AssociateMenuItemsRequest {

  @NonNull private String restaurantId;
  @NonNull private String menuId;
  @NonNull private Set<String> menuItems;
}
