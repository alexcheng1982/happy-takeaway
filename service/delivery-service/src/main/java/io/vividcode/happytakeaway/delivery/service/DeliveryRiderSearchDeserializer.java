package io.vividcode.happytakeaway.delivery.service;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class DeliveryRiderSearchDeserializer extends ObjectMapperDeserializer<DeliveryRiderSearch> {

  public DeliveryRiderSearchDeserializer() {
    super(DeliveryRiderSearch.class);
  }
}
