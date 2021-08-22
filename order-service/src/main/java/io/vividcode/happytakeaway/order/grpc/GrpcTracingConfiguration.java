package io.vividcode.happytakeaway.order.grpc;

import io.opentracing.Tracer;
import io.opentracing.contrib.grpc.ServerTracingInterceptor;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class GrpcTracingConfiguration {

  @Produces
  @ApplicationScoped
  public ServerTracingInterceptor serverTracingInterceptor(Tracer tracer) {
    return new ServerTracingInterceptor(tracer);
  }
}
