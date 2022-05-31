package io.vividcode.happytakeaway.restaurant.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.common.grpc.ResourceNotFoundException;
import io.vividcode.happytakeaway.restaurant.TestHelper;
import io.vividcode.happytakeaway.restaurant.api.AssociateMenuItemsResponse;
import io.vividcode.happytakeaway.restaurant.api.UpdateMenuResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuItem;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("menu service")
class MenuServiceTest {

  @Inject RestaurantService restaurantService;

  @Inject MenuService menuService;

  @Inject MenuItemService menuItemService;

  @Test
  @DisplayName("create a new menu")
  void createMenu() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String id = this.menuService.createMenu(TestHelper.createMenuRequest(restaurantId, false));
    assertThat(id).isNotNull();
  }

  @Test
  @DisplayName("update a menu")
  void updateMenu() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String menuId = this.menuService.createMenu(TestHelper.createMenuRequest(restaurantId, false));
    String newName = "new";
    UpdateMenuResponse response =
        this.menuService.updateMenu(TestHelper.updateMenuRequest(restaurantId, menuId, newName));
    assertThat(response).extracting(UpdateMenuResponse::getName).isEqualTo(newName);
  }

  @Test
  @DisplayName("delete a menu")
  void deleteMenu() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String menuId = this.menuService.createMenu(TestHelper.createMenuRequest(restaurantId, false));
    this.menuService.deleteMenu(TestHelper.deleteMenuRequest(restaurantId, menuId));
    assertThrows(
        ResourceNotFoundException.class,
        () -> this.menuService.getMenu(TestHelper.getMenuRequest(restaurantId, menuId)));
  }

  @Test
  @DisplayName("associate menu items to a menu")
  void associateMenuItems() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String menuId = this.menuService.createMenu(TestHelper.createMenuRequest(restaurantId, false));
    Set<String> menuItemIds =
        IntStream.range(0, 10)
            .mapToObj(
                index ->
                    this.menuItemService.createMenuItem(
                        TestHelper.createMenuItemRequest(restaurantId)))
            .collect(Collectors.toSet());
    AssociateMenuItemsResponse response =
        this.menuService.associateMenuItems(
            TestHelper.associateMenuItemsRequest(restaurantId, menuId, menuItemIds));
    assertThat(response.getMenuItems().size()).isEqualTo(10);
    PagedResult<MenuItem> pagedResult =
        this.menuItemService.findMenuItems(menuId, PageRequest.of(0, 5));
    assertThat(pagedResult.getData()).asList().hasSize(5);
    assertThat(pagedResult.getTotalItems()).isEqualTo(10);
  }
}
