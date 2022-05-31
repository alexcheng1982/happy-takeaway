package io.vividcode.happytakeaway.restaurant.service;

import io.vividcode.happytakeaway.common.grpc.ResourceNotFoundException;
import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuItem;
import io.vividcode.happytakeaway.restaurant.api.v1.Restaurant;
import io.vividcode.happytakeaway.restaurant.entity.MenuItemEntity;
import io.vividcode.happytakeaway.restaurant.entity.RestaurantEntity;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceHelper {

  private ServiceHelper() {}

  public static ResourceNotFoundException restaurantNotFound(String id) {
    return new ResourceNotFoundException("Restaurant", id);
  }

  public static ResourceNotFoundException menuNotFound(String id) {
    return new ResourceNotFoundException("Menu", id);
  }

  public static ResourceNotFoundException menuItemNotFound(String id) {
    return new ResourceNotFoundException("MenuItem", id);
  }

  public static <IN, OUT> List<OUT> transform(
      List<IN> list, Function<? super IN, ? extends OUT> transformer) {
    return list.stream().map(transformer).collect(Collectors.toList());
  }

  public static Restaurant buildRestaurant(RestaurantEntity restaurant) {
    return Restaurant.builder()
        .id(restaurant.getId())
        .name(restaurant.getName())
        .description(restaurant.getDescription())
        .address(ServiceHelper.buildAddress(restaurant))
        .build();
  }

  public static Address buildAddress(RestaurantEntity restaurant) {
    return Address.builder()
        .code(restaurant.getAddressCode())
        .addressLine(restaurant.getAddressLine())
        .lng(restaurant.getAddressLng())
        .lat(restaurant.getAddressLat())
        .build();
  }

  public static MenuItem buildMenuItem(
      MenuItemEntity entity, Function<MenuItemEntity, String> coverImageProvider) {
    return MenuItem.builder()
        .id(entity.getId())
        .name(entity.getName())
        .coverImage(coverImageProvider.apply(entity))
        .description(entity.getDescription())
        .price(entity.getPrice())
        .build();
  }
}
