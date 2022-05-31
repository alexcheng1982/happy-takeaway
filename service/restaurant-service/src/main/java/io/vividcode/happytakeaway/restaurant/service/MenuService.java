package io.vividcode.happytakeaway.restaurant.service;

import io.vividcode.happytakeaway.restaurant.api.AssociateMenuItemsRequest;
import io.vividcode.happytakeaway.restaurant.api.AssociateMenuItemsResponse;
import io.vividcode.happytakeaway.restaurant.api.CreateMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.GetMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuWithItems;
import io.vividcode.happytakeaway.restaurant.entity.MenuEntity;
import io.vividcode.happytakeaway.restaurant.entity.MenuItemEntity;
import io.vividcode.happytakeaway.restaurant.repository.MenuItemRepository;
import io.vividcode.happytakeaway.restaurant.repository.MenuRepository;
import io.vividcode.happytakeaway.restaurant.repository.RestaurantRepository;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class MenuService {

  @Inject RestaurantRepository restaurantRepository;

  @Inject MenuRepository menuRepository;

  @Inject MenuItemRepository menuItemRepository;

  @Inject OwnerIdProvider ownerIdProvider;

  @Inject MenuItemService menuItemService;

  public MenuWithItems getMenu(GetMenuRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant ->
                this.menuRepository
                    .findById(request.getRestaurantId(), request.getMenuId())
                    .map(
                        menu ->
                            MenuWithItems.builder()
                                .id(menu.getId())
                                .name(menu.getName())
                                .items(
                                    this.menuItemService.findMenuItems(
                                        menu.getId(), request.getPageRequest()))
                                .build())
                    .orElseThrow(() -> ServiceHelper.menuNotFound(request.getMenuId())))
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  @Transactional
  public String createMenu(CreateMenuRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant -> {
              MenuEntity entity =
                  MenuEntity.builder().restaurant(restaurant).name(request.getName()).build();
              this.menuRepository.persist(entity);
              if (request.isCurrent()) {
                restaurant.setActiveMenu(entity);
              }
              return entity.getId();
            })
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  @Transactional
  public UpdateMenuResponse updateMenu(UpdateMenuRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant ->
                this.menuRepository
                    .findById(request.getRestaurantId(), request.getId())
                    .map(
                        menu -> {
                          menu.setName(request.getName());
                          this.menuRepository.persist(menu);
                          return menu;
                        })
                    .orElseThrow(() -> ServiceHelper.menuNotFound(request.getId())))
        .map(menu -> UpdateMenuResponse.builder().id(menu.getId()).name(menu.getName()).build())
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  @Transactional
  public boolean deleteMenu(DeleteMenuRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant ->
                this.menuRepository
                    .findById(request.getRestaurantId(), request.getId())
                    .map(
                        menu -> {
                          if (Objects.equals(restaurant.getActiveMenu(), menu)) {
                            restaurant.setActiveMenu(null);
                            this.restaurantRepository.persist(restaurant);
                          }
                          this.menuRepository.delete(menu);
                          return true;
                        })
                    .orElse(false))
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }

  @Transactional
  public AssociateMenuItemsResponse associateMenuItems(AssociateMenuItemsRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant ->
                this.menuRepository
                    .findByIdOptional(request.getMenuId())
                    .map(
                        menu -> {
                          menu.setItems(
                              this.menuItemRepository.findAllByItemIds(
                                  request.getRestaurantId(), request.getMenuItems()));
                          return menu;
                        })
                    .orElseThrow(() -> ServiceHelper.menuNotFound(request.getMenuId())))
        .map(
            menu ->
                AssociateMenuItemsResponse.builder()
                    .menuId(menu.getId())
                    .menuItems(
                        menu.getItems().stream()
                            .map(MenuItemEntity::getId)
                            .collect(Collectors.toSet()))
                    .build())
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
  }
}
