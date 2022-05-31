package io.vividcode.happytakeaway.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_event")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventEntity {

  @Id private String id;

  @Column(name = "type")
  private String type;

  @Column(name = "aggregatetype")
  String aggregateType;

  @Column(name = "aggregateid")
  String aggregateId;

  @Column(name = "payload")
  String payload;

  @Column(name = "timestamp")
  Long timestamp;
}
