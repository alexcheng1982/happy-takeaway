package io.vividcode.happytakeaway.mobileapi;

import io.vividcode.happytakeaway.common.PageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Name;

@Data
@NoArgsConstructor
@Name("FindOrders")
public class FindOrdersInput {

  private String userId;
  private String restaurantId;
  private String status;
  private PageRequest pageRequest;
}
