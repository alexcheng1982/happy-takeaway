package io.vividcode.happytakeaway.restaurant.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vividcode.happytakeaway.common.test.RedisResource;
import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import io.vividcode.happytakeaway.restaurant.api.v1.Restaurant;
import io.vividcode.happytakeaway.restaurant.search.api.v1.GeoLocationSearchRequest;
import io.vividcode.happytakeaway.restaurant.search.api.v1.GeoLocationSearchResponse;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(RedisResource.class)
@DisplayName("Geo location search service")
class GeoLocationSearchServiceTest {

  private static final Faker faker = Faker.instance();

  @Inject GeoLocationSearchService geoLocationSearchService;

  @Inject DataUpdateService dataUpdateService;

  @Test
  @DisplayName("search")
  void search() {
    double centerLng = 0.0;
    double centerLat = 0.0;
    List<Restaurant> restaurants =
        IntStream.range(1, 4)
            .mapToObj(index -> this.createRestaurant(index, centerLng, centerLat))
            .collect(Collectors.toList());
    this.dataUpdateService.updateData(restaurants);
    GeoLocationSearchResponse response =
        this.geoLocationSearchService.search(
            GeoLocationSearchRequest.builder()
                .lng(centerLng)
                .lat(centerLat)
                .radius(30_000)
                .build());
    assertThat(response.getData()).asList().hasSize(3);
  }

  private Restaurant createRestaurant(int index, double lng, double lat) {
    return Restaurant.builder()
        .id(UUID.randomUUID().toString())
        .name("restaurant " + index)
        .address(
            Address.builder()
                .code(faker.number().digits(10))
                .addressLine(faker.address().fullAddress())
                .lng(lng + faker.random().nextInt(-10, 10) * 0.00001)
                .lat(lat + faker.random().nextInt(-10, 10) * 0.00001)
                .build())
        .build();
  }
}
