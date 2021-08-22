package io.vividcode.happytakeaway.common;

import io.grpc.Status;

public interface GrpcStatusAware {

  Status toStatus();
}
