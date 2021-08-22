package io.vividcode.happytakeaway.restaurant.dataloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

public class DataLoaderConfiguration {

  @Produces
  @Singleton
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
