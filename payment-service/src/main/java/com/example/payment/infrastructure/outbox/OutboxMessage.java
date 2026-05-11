package com.example.payment.infrastructure.outbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 36)
  private String eventId;

  @Column(nullable = false, length = 50)
  private String aggregateType;

  @Column(nullable = false, length = 50)
  private String aggregateId;

  @Column(nullable = false, length = 100)
  private String eventType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "JSON")
  private String payload;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OutboxStatus status;

  @Column(nullable = false)
  private int retryCount;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime publishedAt;

  private OutboxMessage(String eventId, String aggregateType, String aggregateId,
      String eventType, String payload) {
    this.eventId = eventId;
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
    this.status = OutboxStatus.PENDING;
    this.retryCount = 0;
    this.createdAt = LocalDateTime.now();
  }

  public static OutboxMessage create(String eventId, String aggregateType,
      String aggregateId, String eventType, String payload) {
    return new OutboxMessage(eventId, aggregateType, aggregateId, eventType, payload);
  }

  public void markPublished() {
    this.status = OutboxStatus.PUBLISHED;
    this.publishedAt = LocalDateTime.now();
  }

  public void markFailed() {
    this.status = OutboxStatus.FAILED;
  }

  public void incrementRetry() {
    this.retryCount++;
  }
}