package io.vividcode.happytakeaway.order.entity;

import io.vividcode.happytakeaway.common.entity.BaseEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "line_items")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineItemEntity extends BaseEntity {

  @Column(name = "item_id", length = 36)
  @Size(max = 36)
  private String itemId;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "price")
  private BigDecimal price;
}
