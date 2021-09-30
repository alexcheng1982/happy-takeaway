package io.vividcode.happytakeaway.restaurant.entity;

import io.vividcode.happytakeaway.common.entity.TimestampedBaseEntity;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"restaurant", "menus"})
@Indexed
public class MenuItemEntity extends TimestampedBaseEntity {

  @ManyToOne
  @JoinColumn(name = "restaurant_id", nullable = false)
  private RestaurantEntity restaurant;

  @Column(name = "name")
  @Size(min = 1, max = 128)
  @FullTextField(analyzer = "cjk")
  private String name;

  @Column(name = "cover_image")
  @Size(max = 256)
  private String coverImage;

  @Column(name = "description")
  @FullTextField(analyzer = "cjk")
  private String description;

  @Column(name = "price")
  @GenericField
  private BigDecimal price;

  @ManyToMany(mappedBy = "items")
  private List<MenuEntity> menus;
}
