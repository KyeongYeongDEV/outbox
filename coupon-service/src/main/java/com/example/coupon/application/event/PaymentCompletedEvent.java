package com.example.coupon.application.event;

import java.time.LocalDateTime;

// payment-service의 동일한 이름 record와 독립된 클래스 (MSA 답게 각자 보유)
public record PaymentCompletedEvent(
    String eventId,
    Long paymentId,
    Long userId,
    Long amount,
    String currency,
    LocalDateTime occurredAt) {
}