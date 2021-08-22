package io.vividcode.happytakeaway.delivery.api.v1;

import io.vividcode.happytakeaway.event.api.AggregateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryTask implements AggregateEntity {

  private String id;
  private String orderId;
  private String restaurantId;
  private String userId;
  private Address restaurantAddress;
  private Address userAddress;
  private OrderInfo orderInfo;

  @Override
  public String aggregateId() {
    return this.id;
  }

  @Override
  public String aggregateType() {
    return "DeliveryTask";
  }
}
