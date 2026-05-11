package com.example.payment.application.event;

import java.time.LocalDateTime;

public record PaymentCompletedEvent(
    String eventId,
    Long paymentId,
    Long userId,
    Long amount,
    String currency,
    LocalDateTime occurredAt) {
}