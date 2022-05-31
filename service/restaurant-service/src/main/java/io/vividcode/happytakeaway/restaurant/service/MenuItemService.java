package io.vividcode.happytakeaway.restaurant.service;

import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.restaurant.api.CreateMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.GetMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuItemResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuItem;
import io.vividcode.happytakeaway.restaurant.entity.MenuEntity;
import io.vividcode.happytakeaway.restaurant.entity.MenuItemEntity;
import io.vividcode.happytakeaway.restaurant.repository.MenuItemRepository;
import io.vividcode.happytakeaway.restaurant.repository.RestaurantRepository;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MenuItemService {

  @Inject RestaurantRepository restaurantRepository;

  @Inject MenuItemRepository menuItemRepository;

  @Inject EntityManager entityManager;

  @Inject OwnerIdProvider ownerIdProvider;

  @ConfigProperty(name = "app.service.file")
  String fileServiceUrl;

  public MenuItem getMenuItem(GetMenuItemRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant ->
                this.menuItemRepository
                    .findById(request.getRestaurantId(), request.getMenuItemId())
                    .map(menuItem -> MenuItem.builder().build())
                    .orElseThrow(() -> ServiceHelper.menuItemNotFound(request.getMenuItemId())))
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  @Transactional
  public String createMenuItem(CreateMenuItemRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant -> {
              MenuItemEntity entity =
                  MenuItemEntity.builder()
                      .restaurant(restaurant)
                      .name(request.getName())
                      .coverImage(request.getCoverImage())
                      .description(request.getDescription())
                      .price(request.getPrice())
                      .build();
              this.menuItemRepository.persist(entity);
              return entity.getId();
            })
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  @Transactional
  public UpdateMenuItemResponse updateMenuItem(UpdateMenuItemRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant ->
                this.menuItemRepository
                    .findById(request.getRestaurantId(), request.getId())
                    .map(
                        item -> {
                          if (request.getName() != null) {
                            item.setName(request.getName());
                          }
                          if (request.getDescription() != null) {
                            item.setDescription(request.getDescription());
                          }
                          if (request.getPrice() != null) {
                            item.setPrice(request.getPrice());
                          }
                          this.menuItemRepository.persist(item);
                          return UpdateMenuItemResponse.builder()
                              .id(item.getId())
                              .name(item.getName())
                              .description(item.getDescription())
                              .price(item.getPrice())
                              .build();
                        })
                    .orElseThrow(() -> ServiceHelper.menuItemNotFound(request.getId())))
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  @Transactional
  public boolean deleteMenuItem(DeleteMenuItemRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant ->
                this.menuItemRepository
                    .findById(request.getRestaurantId(), request.getId())
                    .map(
                        item -> {
                          this.menuItemRepository.delete(item);
                          return true;
                        })
                    .orElse(false))
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  public PagedResult<MenuItem> findMenuItems(String menuId, PageRequest pageRequest) {
    CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<MenuEntity> countQueryRoot = countQuery.from(MenuEntity.class);
    countQuery.where(criteriaBuilder.equal(countQueryRoot.get("id"), menuId));
    long totalResults =
        this.entityManager
            .createQuery(countQuery.select(criteriaBuilder.count(countQueryRoot.join("items"))))
            .getSingleResult();

    CriteriaQuery<MenuItemEntity> itemsQuery = criteriaBuilder.createQuery(MenuItemEntity.class);
    Root<MenuEntity> itemsQueryRoot = itemsQuery.from(MenuEntity.class);
    itemsQuery.where(criteriaBuilder.equal(itemsQueryRoot.get("id"), menuId));
    List<MenuItemEntity> data =
        this.entityManager
            .createQuery(itemsQuery.select(itemsQueryRoot.join("items")).distinct(true))
            .setFirstResult(pageRequest.getOffset())
            .setMaxResults(pageRequest.getSize())
            .getResultList();

    List<MenuItem> results =
        ServiceHelper.transform(
            data, (entity) -> ServiceHelper.buildMenuItem(entity, this::getCoverImageUrl));
    return PagedResult.fromData(results, pageRequest, totalResults);
  }

  private String getCoverImageUrl(MenuItemEntity entity) {
    return this.fileServiceUrl + "/file/" + entity.getCoverImage();
  }
}
