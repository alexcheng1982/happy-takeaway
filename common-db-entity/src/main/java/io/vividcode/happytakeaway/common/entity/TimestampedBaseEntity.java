package io.vividcode.happytakeaway.common.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class TimestampedBaseEntity extends BaseEntity {

  @Column(name = "created_at")
  private Long createdAt;

  @Column(name = "updated_at")
  private Long updatedAt;

  @PrePersist
  void setInitialDate() {
    this.createdAt = this.updatedAt = System.currentTimeMillis();
  }

  @PreUpdate
  void updateDate() {
    this.updatedAt = System.currentTimeMillis();
  }
}
