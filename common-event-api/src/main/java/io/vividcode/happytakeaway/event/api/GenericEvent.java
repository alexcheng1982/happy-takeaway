package io.vividcode.happytakeaway.event.api;

import java.util.UUID;

public class GenericEvent<T> implements DomainEvent<T> {

  private final String eventId;
  private final String eventType;
  private final long timestamp;
  private final T payload;

  public GenericEvent(String eventType, T payload) {
    this.eventId = UUID.randomUUID().toString();
    this.eventType = eventType;
    this.timestamp = System.currentTimeMillis();
    this.payload = payload;
  }

  public GenericEvent(String eventType, String eventId, long timestamp, T payload) {
    this.eventId = eventId;
    this.eventType = eventType;
    this.timestamp = timestamp;
    this.payload = payload;
  }

  @Override
  public String getEventId() {
    return this.eventId;
  }

  @Override
  public long getTimestamp() {
    return this.timestamp;
  }

  @Override
  public T getPayload() {
    return this.payload;
  }

  @Override
  public String getEventType() {
    return this.eventType;
  }
}
