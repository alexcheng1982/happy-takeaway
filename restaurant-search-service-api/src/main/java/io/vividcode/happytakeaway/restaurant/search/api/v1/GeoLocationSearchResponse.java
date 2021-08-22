package io.vividcode.happytakeaway.restaurant.search.api.v1;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoLocationSearchResponse {

  private List<RestaurantWithGeoLocation> data;
}
