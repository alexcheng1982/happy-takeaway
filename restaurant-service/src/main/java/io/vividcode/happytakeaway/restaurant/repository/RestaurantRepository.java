package io.vividcode.happytakeaway.restaurant.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.vividcode.happytakeaway.restaurant.entity.RestaurantEntity;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestaurantRepository implements PanacheRepositoryBase<RestaurantEntity, String> {

  public Optional<RestaurantEntity> findByOwnerIdAndId(String ownerId, String id) {
    return this.find("ownerId = ?1 and id = ?2", ownerId, id).firstResultOptional();
  }

  public boolean deleteByOwnerIdAndId(String ownerId, String id) {
    return this.delete("ownerId = ?1 and id = ?2", ownerId, id) > 0;
  }
}
