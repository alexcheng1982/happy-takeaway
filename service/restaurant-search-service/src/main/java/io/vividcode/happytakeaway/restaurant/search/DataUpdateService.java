package io.vividcode.happytakeaway.restaurant.search;

import static io.vividcode.happytakeaway.restaurant.search.GeoLocationSearchService.REDIS_KEY;

import io.quarkus.redis.client.RedisClient;
import io.vividcode.happytakeaway.restaurant.api.v1.Restaurant;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DataUpdateService {

  @Inject RedisClient redisClient;

  public void updateData(List<Restaurant> restaurants) {
    List<String> args = new ArrayList<>();
    args.add(REDIS_KEY);
    restaurants.forEach(
        restaurant -> {
          args.add(Double.toString(restaurant.getAddress().getLng()));
          args.add(Double.toString(restaurant.getAddress().getLat()));
          args.add(restaurant.getId());
        });
    this.redisClient.geoadd(args);
  }
}
