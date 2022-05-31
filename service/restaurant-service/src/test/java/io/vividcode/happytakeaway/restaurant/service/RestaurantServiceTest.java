package io.vividcode.happytakeaway.restaurant.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.common.grpc.ResourceNotFoundException;
import io.vividcode.happytakeaway.restaurant.ElasticsearchResource;
import io.vividcode.happytakeaway.restaurant.TestHelper;
import io.vividcode.happytakeaway.restaurant.api.UpdateRestaurantResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.MenuItem;
import io.vividcode.happytakeaway.restaurant.api.v1.RestaurantWithMenuItems;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(
    value = ElasticsearchResource.class,
    initArgs = @ResourceArg(name = "version", value = "7.10.2"))
@DisplayName("restaurant service")
class RestaurantServiceTest {

  @Inject RestaurantService restaurantService;

  @Inject MenuService menuService;

  @Inject MenuItemService menuItemService;

  @Test
  @DisplayName("create a new restaurant")
  void createRestaurant() {
    String id = this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    assertThat(id).isNotNull();
  }

  @Test
  @DisplayName("get a restaurant")
  void getRestaurant() {
    String id = this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    RestaurantWithMenuItems restaurant =
        this.restaurantService.getRestaurant(TestHelper.getRestaurantRequest(id, 0));
    assertThat(restaurant).extracting(RestaurantWithMenuItems::getId).isEqualTo(id);
  }

  @Test
  @DisplayName("update a restaurant")
  void updateRestaurant() {
    String id = this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String newName = "new";
    UpdateRestaurantResponse updateRestaurant =
        this.restaurantService.updateRestaurant(TestHelper.updateRestaurantRequest(id, newName));
    assertThat(updateRestaurant).extracting(UpdateRestaurantResponse::getName).isEqualTo(newName);
  }

  @Test
  @DisplayName("delete a restaurant")
  void deleteRestaurant() {
    String id = this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    this.restaurantService.deleteRestaurant(TestHelper.deleteRestaurantRequest(id));
    assertThrows(
        ResourceNotFoundException.class,
        () -> this.restaurantService.getRestaurant(TestHelper.getRestaurantRequest(id, 0)));
  }

  @Test
  @DisplayName("set active menu")
  void setActiveMenu() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String menuId = this.menuService.createMenu(TestHelper.createMenuRequest(restaurantId, false));
    String menuItemId =
        this.menuItemService.createMenuItem(TestHelper.createMenuItemRequest(restaurantId));
    this.menuService.associateMenuItems(
        TestHelper.associateMenuItemsRequest(restaurantId, menuId, Set.of(menuItemId)));
    this.restaurantService.setActiveMenu(TestHelper.setActiveMenuRequest(restaurantId, menuId));
    RestaurantWithMenuItems restaurant =
        this.restaurantService.getRestaurant(TestHelper.getRestaurantRequest(restaurantId, 10));
    assertThat(restaurant.getMenuItems().getTotalItems()).isEqualTo(1);
  }

  @Test
  @DisplayName("pagination of menu items")
  void testMenuItemPagination() {
    String restaurantId =
        this.restaurantService.createRestaurant(TestHelper.createRestaurantRequest());
    String menuId = this.menuService.createMenu(TestHelper.createMenuRequest(restaurantId, true));
    int numberOfMenuItems = 21;
    Set<String> menuItemIds =
        IntStream.range(0, numberOfMenuItems)
            .mapToObj(
                index ->
                    this.menuItemService.createMenuItem(
                        TestHelper.createMenuItemRequest(restaurantId)))
            .collect(Collectors.toSet());
    this.menuService.associateMenuItems(
        TestHelper.associateMenuItemsRequest(restaurantId, menuId, menuItemIds));
    PagedResult<MenuItem> result =
        this.restaurantService.getMenuItems(restaurantId, PageRequest.of(0, 10));
    assertThat(result.getTotalItems()).isEqualTo(numberOfMenuItems);
    assertThat(result.getData()).asList().hasSize(10);
    result = this.restaurantService.getMenuItems(restaurantId, PageRequest.of(1, 10));
    assertThat(result.getData()).asList().hasSize(10);
    result = this.restaurantService.getMenuItems(restaurantId, PageRequest.of(2, 10));
    assertThat(result.getData()).asList().hasSize(1);
  }
}
