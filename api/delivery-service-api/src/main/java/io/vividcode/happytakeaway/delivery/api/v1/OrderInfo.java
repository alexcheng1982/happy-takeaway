package io.vividcode.happytakeaway.delivery.api.v1;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {

  private List<OrderItem> items;
}
