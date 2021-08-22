package io.vividcode.happytakeaway.event.api;

public interface DomainEvent<T> {

  String getEventId();

  long getTimestamp();

  T getPayload();

  default String getEventType() {
    return this.getClass().getName();
  }
}
