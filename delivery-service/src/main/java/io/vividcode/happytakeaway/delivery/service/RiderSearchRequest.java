package io.vividcode.happytakeaway.delivery.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiderSearchRequest {

  private double lng;
  private double lat;
  private double radius;
  private int count;
}
