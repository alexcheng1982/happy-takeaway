package io.vividcode.happytakeaway.restaurant.resource;

import io.vividcode.happytakeaway.common.grpc.ResourceNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

  @Override
  public Response toResponse(ResourceNotFoundException e) {
    return Response.status(Response.Status.NOT_FOUND).build();
  }
}
