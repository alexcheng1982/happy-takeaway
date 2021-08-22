package io.vividcode.happytakeaway.restaurant.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.vividcode.happytakeaway.restaurant.entity.MenuItemEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MenuItemRepository implements PanacheRepositoryBase<MenuItemEntity, String> {

  public Optional<MenuItemEntity> findById(String restaurantId, String menuItemId) {
    return this.find("restaurant.id = ?1 and id = ?2", restaurantId, menuItemId)
        .firstResultOptional();
  }

  public List<MenuItemEntity> findAllByItemIds(String restaurantId, Set<String> ids) {
    return this.find("restaurant.id = ?1 and id in (?2)", restaurantId, ids).list();
  }
}
