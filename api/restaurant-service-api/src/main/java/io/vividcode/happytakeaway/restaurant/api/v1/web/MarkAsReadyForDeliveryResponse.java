package io.vividcode.happytakeaway.restaurant.api.v1.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadyForDeliveryResponse {

  private String orderId;
  private boolean result;
}
