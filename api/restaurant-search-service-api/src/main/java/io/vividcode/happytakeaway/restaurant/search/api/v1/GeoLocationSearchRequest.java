package io.vividcode.happytakeaway.restaurant.search.api.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoLocationSearchRequest {

  private double lng;
  private double lat;
  private long radius;
}
