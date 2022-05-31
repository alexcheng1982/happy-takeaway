package io.vividcode.happytakeaway.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.restaurant.ElasticsearchResource;
import io.vividcode.happytakeaway.restaurant.TestHelper;
import io.vividcode.happytakeaway.restaurant.api.v1.Restaurant;
import io.vividcode.happytakeaway.restaurant.entity.RestaurantEntity;
import io.vividcode.happytakeaway.restaurant.repository.RestaurantRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
@QuarkusTestResource(ElasticsearchResource.class)
@DisplayName("restaurant service")
public class RestaurantServiceMockTest {

  @InjectMock RestaurantRepository restaurantRepository;

  @Inject RestaurantService restaurantService;

  @Test
  @DisplayName("list restaurants")
  void listRestaurants() {
    PanacheQuery<RestaurantEntity> listQuery = Mockito.mock(PanacheQuery.class);
    Mockito.when(listQuery.list())
        .thenReturn(
            IntStream.range(0, 10)
                .mapToObj(index -> TestHelper.createRestaurantEntity())
                .collect(Collectors.toList()));
    PanacheQuery<RestaurantEntity> allQuery = Mockito.mock(PanacheQuery.class);
    Mockito.when(allQuery.page(0, 10)).thenReturn(listQuery);
    Mockito.when(this.restaurantRepository.findAll()).thenReturn(allQuery);
    List<Restaurant> result = this.restaurantService.listAllRestaurants(PageRequest.of(0, 10));
    assertThat(result).asList().hasSize(10);
  }
}
