package io.vividcode.happytakeaway.order.service;

import io.grpc.Status;
import io.vividcode.happytakeaway.common.grpc.GrpcStatusAware;
import io.vividcode.happytakeaway.order.entity.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException implements GrpcStatusAware {

  private static final long serialVersionUID = 1769239078860483246L;
  private final OrderStatus requiredStatus;
  private final OrderStatus actualStatus;

  public InvalidOrderStatusException(OrderStatus requiredStatus, OrderStatus actualStatus) {
    this.requiredStatus = requiredStatus;
    this.actualStatus = actualStatus;
  }

  @Override
  public Status toStatus() {
    return Status.FAILED_PRECONDITION.withDescription(this.getMessage());
  }

  @Override
  public String getMessage() {
    return String.format(
        "Invalid order status, required status = %s, actual status = %s",
        this.requiredStatus, this.actualStatus);
  }

  public OrderStatus getRequiredStatus() {
    return this.requiredStatus;
  }

  public OrderStatus getActualStatus() {
    return this.actualStatus;
  }
}
