package io.vividcode.happytakeaway.event;

import io.vividcode.happytakeaway.common.json.JsonMapper;
import io.vividcode.happytakeaway.event.api.AggregateEntity;
import io.vividcode.happytakeaway.event.api.DomainEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@ApplicationScoped
public class EventService {

  @Inject EventRepository eventRepository;

  @Transactional(TxType.MANDATORY)
  public <A extends AggregateEntity, E extends DomainEvent<?>> void publish(A aggregate, E event) {
    EventEntity entity =
        EventEntity.builder()
            .id(event.getEventId())
            .type(event.getEventType())
            .timestamp(event.getTimestamp())
            .aggregateType(aggregate.aggregateType())
            .aggregateId(aggregate.aggregateId())
            .payload(this.convertEventPayload(event.getPayload()))
            .build();
    this.eventRepository.persist(entity);
  }

  private String convertEventPayload(Object payload) {
    try {
      return JsonMapper.toJson(payload);
    } catch (Exception e) {
      return "{}";
    }
  }
}
