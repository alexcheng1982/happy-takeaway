package io.vividcode.happytakeaway.restaurant.service;

import io.quarkus.panache.common.Sort;
import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.restaurant.api.CreateRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.GetRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.SetActiveMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.SetActiveMenuResponse;
import io.vividcode.happytakeaway.restaurant.api.UpdateRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateRestaurantResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuItem;
import io.vividcode.happytakeaway.restaurant.api.v1.Restaurant;
import io.vividcode.happytakeaway.restaurant.api.v1.RestaurantWithMenuItems;
import io.vividcode.happytakeaway.restaurant.api.v1.RestaurantWithMenuItems.RestaurantWithMenuItemsBuilder;
import io.vividcode.happytakeaway.restaurant.entity.RestaurantEntity;
import io.vividcode.happytakeaway.restaurant.entity.RestaurantEntity.RestaurantEntityBuilder;
import io.vividcode.happytakeaway.restaurant.repository.MenuRepository;
import io.vividcode.happytakeaway.restaurant.repository.RestaurantRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class RestaurantService {

  @Inject RestaurantRepository restaurantRepository;

  @Inject MenuRepository menuRepository;

  @Inject MenuItemService menuItemService;

  @Inject OwnerIdProvider ownerIdProvider;

  @Transactional
  public String createRestaurant(CreateRestaurantRequest request) {
    RestaurantEntityBuilder builder =
        RestaurantEntity.builder()
            .ownerId(this.ownerIdProvider.get())
            .name(request.getName())
            .description(request.getDescription())
            .phoneNumber(request.getPhoneNumber());
    if (request.getAddress() != null) {
      builder
          .addressCode(request.getAddress().getCode())
          .addressLine(request.getAddress().getAddressLine())
          .addressLng(request.getAddress().getLng())
          .addressLat(request.getAddress().getLat());
    }
    RestaurantEntity entity = builder.build();
    this.restaurantRepository.persist(entity);
    return entity.getId();
  }

  public RestaurantWithMenuItems getRestaurant(GetRestaurantRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getId())
        .map(
            restaurant -> {
              RestaurantWithMenuItemsBuilder builder =
                  RestaurantWithMenuItems.builder()
                      .id(restaurant.getId())
                      .name(restaurant.getName())
                      .description(restaurant.getDescription())
                      .address(ServiceHelper.buildAddress(restaurant));
              if (restaurant.getActiveMenu() != null && request.getNumberOfMenuItems() > 0) {
                builder.menuItems(
                    this.menuItemService.findMenuItems(
                        restaurant.getActiveMenu().getId(),
                        PageRequest.of(0, request.getNumberOfMenuItems())));
              }
              return builder.build();
            })
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getId()));
  }

  @Transactional
  public List<Restaurant> listAllRestaurants(PageRequest pageRequest) {
    return this.restaurantRepository
        .findAll()
        .page(pageRequest.getPage(), pageRequest.getSize())
        .list()
        .stream()
        .map(ServiceHelper::buildRestaurant)
        .collect(Collectors.toList());
  }

  @Transactional
  public PagedResult<Restaurant> listRestaurants(PageRequest pageRequest) {
    long count = this.restaurantRepository.count();
    List<RestaurantEntity> entities =
        this.restaurantRepository
            .findAll(Sort.by("name"))
            .page(pageRequest.getPage(), pageRequest.getSize())
            .list();
    List<Restaurant> results = ServiceHelper.transform(entities, ServiceHelper::buildRestaurant);
    return PagedResult.fromData(results, pageRequest, count);
  }

  @Transactional
  public PagedResult<MenuItem> getMenuItems(String restaurantId, PageRequest pageRequest) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), restaurantId)
        .map(
            restaurant ->
                Optional.ofNullable(restaurant.getActiveMenu())
                    .map(menu -> this.menuItemService.findMenuItems(menu.getId(), pageRequest))
                    .orElse(PagedResult.empty()))
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(restaurantId));
  }

  @Transactional
  public UpdateRestaurantResponse updateRestaurant(UpdateRestaurantRequest request) {
    return this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getId())
        .map(
            restaurant -> {
              if (request.getName() != null) {
                restaurant.setName(request.getName());
              }
              if (request.getDescription() != null) {
                restaurant.setDescription(request.getDescription());
              }
              Address address = request.getAddress();
              if (address != null) {
                restaurant.setAddressCode(address.getCode());
                restaurant.setAddressLine(address.getAddressLine());
                restaurant.setAddressLng(address.getLng());
                restaurant.setAddressLat(address.getLat());
              }
              this.restaurantRepository.persist(restaurant);
              return UpdateRestaurantResponse.builder()
                  .id(restaurant.getId())
                  .name(restaurant.getName())
                  .description(restaurant.getDescription())
                  .address(
                      Address.builder()
                          .code(restaurant.getAddressCode())
                          .addressLine(restaurant.getAddressLine())
                          .lng(restaurant.getAddressLng())
                          .lat(restaurant.getAddressLat())
                          .build())
                  .build();
            })
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getId()));
  }

  @Transactional
  public boolean deleteRestaurant(DeleteRestaurantRequest request) {
    return this.restaurantRepository.deleteByOwnerIdAndId(
        this.ownerIdProvider.get(), request.getId());
  }

  @Transactional
  public SetActiveMenuResponse setActiveMenu(SetActiveMenuRequest request) {
    this.restaurantRepository
        .findByOwnerIdAndId(this.ownerIdProvider.get(), request.getRestaurantId())
        .map(
            restaurant -> {
              restaurant.setActiveMenu(
                  this.menuRepository
                      .findById(request.getRestaurantId(), request.getMenuId())
                      .orElseThrow(() -> ServiceHelper.menuNotFound(request.getMenuId())));
              return restaurant;
            })
        .orElseThrow(() -> ServiceHelper.restaurantNotFound(request.getRestaurantId()));
    return SetActiveMenuResponse.builder()
        .restaurantId(request.getRestaurantId())
        .menuId(request.getMenuId())
        .build();
  }
}
