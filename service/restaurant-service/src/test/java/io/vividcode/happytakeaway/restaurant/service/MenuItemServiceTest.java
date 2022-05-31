package io.vividcode.happytakeaway.restaurant.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import io.vividcode.happytakeaway.common.grpc.ResourceNotFoundException;
import io.vividcode.happytakeaway.restaurant.TestHelper;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuItemResponse;
import javax.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("menu item service")
class MenuItemServiceTest {

  @Inject RestaurantService restaurantService;

  @Inject MenuItemService menuItemService;

  @Test
  @DisplayName("create menu item")
  void createMenuItem() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String id = this.menuItemService.createMenuItem(TestHelper.createMenuItemRequest(restaurantId));
    assertThat(id).isNotNull();
  }

  @Test
  @DisplayName("update menu item")
  void updateMenuItem() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String menuItemId =
        this.menuItemService.createMenuItem(TestHelper.createMenuItemRequest(restaurantId));
    String newName = "new";
    UpdateMenuItemResponse response =
        this.menuItemService.updateMenuItem(
            TestHelper.updateMenuItemRequest(restaurantId, menuItemId, newName));
    assertThat(response.getName()).isEqualTo(newName);
  }

  @Test
  @DisplayName("delete menu item")
  void deleteMenuItem() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String menuItemId =
        this.menuItemService.createMenuItem(TestHelper.createMenuItemRequest(restaurantId));
    this.menuItemService.deleteMenuItem(TestHelper.deleteMenuItemRequest(restaurantId, menuItemId));
    assertThrows(
        ResourceNotFoundException.class,
        () ->
            this.menuItemService.getMenuItem(
                TestHelper.getMenuItemRequest(restaurantId, menuItemId)));
  }
}
