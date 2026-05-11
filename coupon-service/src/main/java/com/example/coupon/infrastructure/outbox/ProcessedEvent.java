package com.example.coupon.infrastructure.outbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEvent {

  @EmbeddedId
  private ProcessedEventId id;

  @Column(nullable = false)
  private LocalDateTime processedAt;

  private ProcessedEvent(String eventId, String consumerGroup) {
    this.id = new ProcessedEventId(eventId, consumerGroup);
    this.processedAt = LocalDateTime.now();
  }

  public static ProcessedEvent of(String eventId, String consumerGroup) {
    return new ProcessedEvent(eventId, consumerGroup);
  }
}