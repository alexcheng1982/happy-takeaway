package io.vividcode.happytakeaway.restaurant.entity;

import io.vividcode.happytakeaway.common.entity.TimestampedBaseEntity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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

@Entity
@Table(name = "menus")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"restaurant"})
public class MenuEntity extends TimestampedBaseEntity {

  @Column(name = "name")
  @Size(min = 1, max = 128)
  private String name;

  @ManyToOne
  @JoinColumn(name = "restaurant_id", nullable = false)
  private RestaurantEntity restaurant;

  @ManyToMany
  @JoinTable(
      name = "menu_item_association",
      joinColumns = @JoinColumn(name = "menu_id"),
      inverseJoinColumns = @JoinColumn(name = "menu_item_id"))
  private List<MenuItemEntity> items;
}
