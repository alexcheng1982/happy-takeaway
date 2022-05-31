package io.vividcode.happytakeaway.restaurant.api.v1.web;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociateMenuItemsWebRequest {

  @NonNull private Set<String> menuItems;
}
