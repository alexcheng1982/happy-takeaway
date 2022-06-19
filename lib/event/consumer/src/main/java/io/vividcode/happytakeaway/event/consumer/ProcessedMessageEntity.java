package io.vividcode.happytakeaway.event.consumer;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(ProcessedMessageId.class)
@Table(name = "processed_messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessedMessageEntity extends PanacheEntityBase {

  @Id
  @Column(name = "consumer_id", length = 36)
  private String consumerId;

  @Id
  @Column(name = "message_id", length = 36)
  private String messageId;

  @Column(name = "timestamp")
  private long timestamp;
}
