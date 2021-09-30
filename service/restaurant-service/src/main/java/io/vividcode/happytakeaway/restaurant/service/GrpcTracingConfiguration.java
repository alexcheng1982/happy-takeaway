package io.vividcode.happytakeaway.restaurant.service;

import io.opentracing.Tracer;
import io.opentracing.contrib.grpc.ClientTracingInterceptor;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class GrpcTracingConfiguration {

  @Produces
  @ApplicationScoped
  public ClientTracingInterceptor clientTracingInterceptor(Tracer tracer) {
    return new ClientTracingInterceptor(tracer);
  }
}
