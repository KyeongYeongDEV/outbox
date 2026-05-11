package com.example.coupon.application.event;

import java.time.LocalDateTime;

public record CouponIssuedEvent(
    String eventId,
    Long couponId,
    Long userId,
    Long paymentId,
    String discountType,
    Long discountValue,
    LocalDateTime occurredAt) {
}