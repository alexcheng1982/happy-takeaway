package io.vividcode.happytakeaway.restaurant.api.v1.web;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociateMenuItemsWebResponse {

  private String menuId;
  private Set<String> menuItems;
}
