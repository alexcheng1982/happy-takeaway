package io.vividcode.happytakeaway.restaurant;

import com.github.javafaker.Faker;
import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.restaurant.api.AssociateMenuItemsRequest;
import io.vividcode.happytakeaway.restaurant.api.CreateMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.CreateMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.CreateRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.DeleteRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.GetMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.GetMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.GetRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.SetActiveMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuItemRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuRequest;
import io.vividcode.happytakeaway.restaurant.api.UpdateRestaurantRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import io.vividcode.happytakeaway.restaurant.entity.RestaurantEntity;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class TestHelper {

  private TestHelper() {}

  private static final Faker faker = Faker.instance();

  public static RestaurantEntity createRestaurantEntity() {
    com.github.javafaker.Address address = faker.address();
    RestaurantEntity restaurant =
        RestaurantEntity.builder()
            .name(faker.funnyName().name())
            .description(faker.lorem().paragraph(10))
            .phoneNumber(faker.phoneNumber().phoneNumber())
            .ownerId(uuid())
            .addressCode(String.valueOf(faker.random().nextInt(10000)))
            .addressLine(address.streetAddress())
            .addressLng(Double.parseDouble(address.longitude()))
            .addressLat(Double.parseDouble(address.latitude()))
            .build();
    restaurant.generateId();
    return restaurant;
  }

  public static GetRestaurantRequest getRestaurantRequest(String id, int numberOfMenuItems) {
    return GetRestaurantRequest.builder().id(id).numberOfMenuItems(numberOfMenuItems).build();
  }

  public static CreateRestaurantRequest createRestaurantRequest() {
    return CreateRestaurantRequest.builder()
        .name(faker.funnyName().name())
        .description(faker.lorem().paragraph(10))
        .phoneNumber(faker.phoneNumber().phoneNumber())
        .address(address())
        .build();
  }

  public static UpdateRestaurantRequest updateRestaurantRequest(String id, String name) {
    return UpdateRestaurantRequest.builder().id(id).name(name).build();
  }

  public static DeleteRestaurantRequest deleteRestaurantRequest(String id) {
    return DeleteRestaurantRequest.builder().id(id).build();
  }

  public static Address address() {
    com.github.javafaker.Address address = faker.address();
    return Address.builder()
        .code(String.valueOf(faker.random().nextInt(10000)))
        .addressLine(address.streetAddress())
        .lng(Double.parseDouble(address.longitude()))
        .lat(Double.parseDouble(address.latitude()))
        .build();
  }

  public static GetMenuRequest getMenuRequest(String restaurantId, String menuId) {
    return GetMenuRequest.builder()
        .restaurantId(restaurantId)
        .menuId(menuId)
        .pageRequest(PageRequest.of(0, 1))
        .build();
  }

  public static CreateMenuRequest createMenuRequest(String restaurantId, boolean current) {
    return CreateMenuRequest.builder()
        .restaurantId(restaurantId)
        .name(faker.pokemon().name())
        .current(current)
        .build();
  }

  public static UpdateMenuRequest updateMenuRequest(
      String restaurantId, String menuId, String name) {
    return UpdateMenuRequest.builder().restaurantId(restaurantId).id(menuId).name(name).build();
  }

  public static DeleteMenuRequest deleteMenuRequest(String restaurantId, String menuId) {
    return DeleteMenuRequest.builder().restaurantId(restaurantId).id(menuId).build();
  }

  public static SetActiveMenuRequest setActiveMenuRequest(String restaurantId, String menuId) {
    return SetActiveMenuRequest.builder().restaurantId(restaurantId).menuId(menuId).build();
  }

  public static AssociateMenuItemsRequest associateMenuItemsRequest(
      String restaurantId, String menuId, Set<String> menuItemIds) {
    return AssociateMenuItemsRequest.builder()
        .restaurantId(restaurantId)
        .menuId(menuId)
        .menuItems(menuItemIds)
        .build();
  }

  public static GetMenuItemRequest getMenuItemRequest(String restaurantId, String menuItemId) {
    return GetMenuItemRequest.builder().restaurantId(restaurantId).menuItemId(menuItemId).build();
  }

  public static CreateMenuItemRequest createMenuItemRequest(String restaurantId) {
    return CreateMenuItemRequest.builder()
        .restaurantId(restaurantId)
        .name(faker.pokemon().name())
        .description(faker.lorem().paragraph(5))
        .price(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 1000)))
        .build();
  }

  public static UpdateMenuItemRequest updateMenuItemRequest(
      String restaurantId, String menuItemId, String name) {
    return UpdateMenuItemRequest.builder()
        .restaurantId(restaurantId)
        .id(menuItemId)
        .name(name)
        .build();
  }

  public static DeleteMenuItemRequest deleteMenuItemRequest(
      String restaurantId, String menuItemId) {
    return DeleteMenuItemRequest.builder().restaurantId(restaurantId).id(menuItemId).build();
  }

  public static String uuid() {
    return UUID.randomUUID().toString();
  }
}
