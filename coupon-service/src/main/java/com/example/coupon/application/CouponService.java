package com.example.coupon.application;

import com.example.coupon.application.event.CouponIssuedEvent;
import com.example.coupon.application.event.PaymentCompletedEvent;
import com.example.coupon.domain.Coupon;
import com.example.coupon.domain.CouponRepository;
import com.example.coupon.domain.DiscountType;
import com.example.coupon.infrastructure.outbox.OutboxMessage;
import com.example.coupon.infrastructure.outbox.OutboxMessageRepository;
import com.example.coupon.infrastructure.outbox.ProcessedEvent;
import com.example.coupon.infrastructure.outbox.ProcessedEventId;
import com.example.coupon.infrastructure.outbox.ProcessedEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

  private static final String CONSUMER_GROUP = "coupon-service";

  private final CouponRepository couponRepository;
  private final OutboxMessageRepository outboxRepository;
  private final ProcessedEventRepository processedEventRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void issueFromPayment(PaymentCompletedEvent event) {
    // 멱등성 체크
    // processed_events PK(event_id, consumer_group) 충돌 시 → 이미 처리된 이벤트
    ProcessedEventId processedId = new ProcessedEventId(event.eventId(), CONSUMER_GROUP);
    if (processedEventRepository.existsById(processedId)) {
      log.warn("[Coupon] 중복 이벤트 skip eventId={}", event.eventId());
      return;
    }

    // 쿠폰 발급 정책
    DiscountType discountType;
    long discountValue;

    if (event.amount() >= 100_000) {
      discountType = DiscountType.FIXED;
      discountValue = 5_000L;
    } else if (event.amount() >= 50_000) {
      discountType = DiscountType.FIXED;
      discountValue = 2_000L;
    } else {
      discountType = DiscountType.PERCENTAGE;
      discountValue = 5L;
    }

    // 1. 쿠폰 저장
    Coupon coupon = Coupon.create(event.userId(), event.paymentId(), discountType, discountValue);
    couponRepository.save(coupon);

    // 2. 처리 완료 기록 — 한 트랜잭션 안에서 같이 커밋
    processedEventRepository.save(ProcessedEvent.of(event.eventId(), CONSUMER_GROUP));

    // 3. 후속 이벤트 outbox 저장
    String newEventId = UUID.randomUUID().toString();
    CouponIssuedEvent couponEvent = new CouponIssuedEvent(
        newEventId,
        coupon.getId(),
        coupon.getUserId(),
        coupon.getPaymentId(),
        coupon.getDiscountType().name(),
        coupon.getDiscountValue(),
        LocalDateTime.now());

    outboxRepository.save(OutboxMessage.create(
        newEventId, "Coupon", String.valueOf(coupon.getId()),
        "CouponIssued", toJson(couponEvent)));

    log.info("[Coupon] 발급 완료 couponId={} userId={} discount={}{}",
        coupon.getId(), event.userId(),
        discountValue, discountType == DiscountType.FIXED ? "원" : "%");

  }

  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("이벤트 직렬화 실패", e);
    }
  }
}