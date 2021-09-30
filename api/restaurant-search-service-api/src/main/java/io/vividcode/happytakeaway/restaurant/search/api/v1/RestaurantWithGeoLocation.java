package io.vividcode.happytakeaway.restaurant.search.api.v1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantWithGeoLocation {

  private String id;
  private double distance;
  private double lng;
  private double lat;
}
