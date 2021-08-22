package io.vividcode.happytakeaway.event.api;

public interface AggregateEntity {

  String aggregateId();

  default String aggregateType() {
    return this.getClass().getName();
  }
}
