package io.vividcode.happytakeaway.order.service;

import io.vividcode.happytakeaway.common.grpc.ResourceNotFoundException;

public class ServiceHelper {

  private ServiceHelper() {}

  public static ResourceNotFoundException orderNotFound(String id) {
    return new ResourceNotFoundException("Order", id);
  }
}
