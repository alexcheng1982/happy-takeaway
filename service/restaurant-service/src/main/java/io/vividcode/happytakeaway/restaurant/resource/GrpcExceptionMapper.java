package io.vividcode.happytakeaway.restaurant.resource;

import io.grpc.StatusRuntimeException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GrpcExceptionMapper implements ExceptionMapper<StatusRuntimeException> {

  @Override
  public Response toResponse(StatusRuntimeException exception) {
    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.TEXT_PLAIN)
        .entity(exception.getMessage())
        .build();
  }
}
