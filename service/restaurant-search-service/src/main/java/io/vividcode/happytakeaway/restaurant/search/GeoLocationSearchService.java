package io.vividcode.happytakeaway.restaurant.search;

import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import io.vividcode.happytakeaway.restaurant.search.api.v1.GeoLocationSearchRequest;
import io.vividcode.happytakeaway.restaurant.search.api.v1.GeoLocationSearchResponse;
import io.vividcode.happytakeaway.restaurant.search.api.v1.RestaurantWithGeoLocation;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GeoLocationSearchService {

  public static final String REDIS_KEY = "restaurant-search";

  @Inject RedisClient redisClient;

  public GeoLocationSearchResponse search(GeoLocationSearchRequest request) {
    Response response =
        this.redisClient.georadius(
            List.of(
                REDIS_KEY,
                Double.toString(request.getLng()),
                Double.toString(request.getLat()),
                Long.toString(request.getRadius()),
                "m",
                "WITHDIST",
                "WITHCOORD"));
    List<RestaurantWithGeoLocation> restaurants =
        response.stream()
            .map(
                r ->
                    RestaurantWithGeoLocation.builder()
                        .id(r.get(0).toString())
                        .distance(Double.parseDouble(r.get(1).toString()))
                        .lng(Double.parseDouble(r.get(2).get(0).toString()))
                        .lat(Double.parseDouble(r.get(2).get(1).toString()))
                        .build())
            .collect(Collectors.toList());
    return GeoLocationSearchResponse.builder().data(restaurants).build();
  }
}
