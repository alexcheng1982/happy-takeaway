package io.vividcode.happytakeaway.restaurant.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.vividcode.happytakeaway.restaurant.entity.MenuEntity;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MenuRepository implements PanacheRepositoryBase<MenuEntity, String> {

  public Optional<MenuEntity> findById(String restaurantId, String menuId) {
    return this.find("restaurant.id = ?1 and id = ?2", restaurantId, menuId).firstResultOptional();
  }
}
