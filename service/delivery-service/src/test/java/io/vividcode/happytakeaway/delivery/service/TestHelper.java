package io.vividcode.happytakeaway.delivery.service;

import io.vividcode.happytakeaway.delivery.api.v1.UpdateRiderPositionRequest;

public class TestHelper {

  public static UpdateRiderPositionRequest updateRiderPositionRequest(
      String riderId, double lng, double lat) {
    return UpdateRiderPositionRequest.newBuilder()
        .setRiderId(riderId)
        .setLng(lng)
        .setLat(lat)
        .setTimestamp(System.currentTimeMillis())
        .build();
  }
}
