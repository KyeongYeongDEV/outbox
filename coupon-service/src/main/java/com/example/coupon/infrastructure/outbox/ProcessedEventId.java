package com.example.coupon.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProcessedEventId implements Serializable {

  @Column(name = "event_id", length = 36)
  private String eventId;

  @Column(name = "consumer_group", length = 100)
  private String consumerGroup;
}