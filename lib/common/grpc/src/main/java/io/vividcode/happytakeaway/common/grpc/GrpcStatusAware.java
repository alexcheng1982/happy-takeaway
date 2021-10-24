package io.vividcode.happytakeaway.common.grpc;

import io.grpc.Status;

public interface GrpcStatusAware {

  Status toStatus();
}
