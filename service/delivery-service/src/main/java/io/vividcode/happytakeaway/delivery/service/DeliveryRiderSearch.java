package io.vividcode.happytakeaway.delivery.service;

import io.vividcode.happytakeaway.delivery.api.v1.DeliveryTask;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryRiderSearch {

  private DeliveryTask deliveryTask;
  private int tryCount;
}
