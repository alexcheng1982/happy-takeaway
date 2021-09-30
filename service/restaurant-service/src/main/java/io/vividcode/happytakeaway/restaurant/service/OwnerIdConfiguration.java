package io.vividcode.happytakeaway.restaurant.service;

import io.quarkus.arc.DefaultBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class OwnerIdConfiguration {

  @ApplicationScoped
  @Produces
  @DefaultBean
  public OwnerIdProvider staticProvider() {
    return () -> "default-user";
  }
}
