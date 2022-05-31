package io.vividcode.happytakeaway.common.grpc;

import io.grpc.Status;

public class ResourceNotFoundException extends RuntimeException implements GrpcStatusAware {

  private final String resourceType;
  private final String resourceId;

  public ResourceNotFoundException(String resourceType, String resourceId) {
    this.resourceType = resourceType;
    this.resourceId = resourceId;
  }

  @Override
  public String getMessage() {
    return String.format(
        "Resource [%s] of type [%s] not found", this.resourceId, this.resourceType);
  }

  @Override
  public Status toStatus() {
    return Status.NOT_FOUND.withDescription(this.getMessage());
  }
}
