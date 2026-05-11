package com.example.payment.application;

import com.example.payment.application.event.PaymentCompletedEvent;
import com.example.payment.domain.Payment;
import com.example.payment.domain.PaymentRepository;
import com.example.payment.infrastructure.outbox.OutboxMessage;
import com.example.payment.infrastructure.outbox.OutboxMessageRepository;
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
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final OutboxMessageRepository outboxRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public Long pay(Long userId, Long amount, String currency) {
    // 1. 결제 저장
    Payment payment = Payment.create(userId, amount, currency);
    paymentRepository.save(payment);

    // 2. eventId 생성 — 페이로드와 outbox row가 동일한 ID 공유
    String eventId = UUID.randomUUID().toString();

    // 3. 이벤트 구성
    PaymentCompletedEvent event = new PaymentCompletedEvent(
        eventId,
        payment.getId(),
        payment.getUserId(),
        payment.getAmount(),
        payment.getCurrency(),
        LocalDateTime.now());

    // 4. outbox INSERT — payments INSERT와 같은 트랜잭션
    // 둘 다 커밋되거나 둘 다 롤백 → Dual Write Problem 해결
    OutboxMessage outboxMessage = OutboxMessage.create(
        eventId,
        "Payment",
        String.valueOf(payment.getId()),
        "PaymentCompleted",
        toJson(event));
    outboxRepository.save(outboxMessage);

    log.info("[Payment] paymentId={} outboxEventId={}", payment.getId(), eventId);
    return payment.getId();
  }

  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("이벤트 직렬화 실패", e);
    }
  }
}