package io.vividcode.happytakeaway.order.entity;

import io.vividcode.happytakeaway.common.entity.TimestampedBaseEntity;
import io.vividcode.happytakeaway.event.api.AggregateEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderEntity extends TimestampedBaseEntity implements AggregateEntity {

  @Override
  public String aggregateId() {
    return this.getId();
  }

  @Override
  public String aggregateType() {
    return "Order";
  }

  @Column(name = "user_id")
  private String userId;

  @Column(name = "restaurant_id", length = 36)
  private String restaurantId;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
  @Builder.Default
  private List<LineItemEntity> lineItems = new ArrayList<>();
}
