package com.example.coupon.infrastructure.messaging;

import com.example.coupon.application.CouponService;
import com.example.coupon.application.event.PaymentCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

  private final CouponService couponService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "payment.PaymentCompleted", groupId = "coupon-service")
  public void onPaymentCompleted(ConsumerRecord<String, String> record, Acknowledgment ack) {
    log.info("[Consumer] 수신 topic={} partition={} offset={}",
        record.topic(), record.partition(), record.offset());
    try {
      PaymentCompletedEvent event = objectMapper.readValue(record.value(), PaymentCompletedEvent.class);

      couponService.issueFromPayment(event);

      // 처리 성공 후 수동 커밋
      // 커밋 전에 앱이 죽으면 → 재처리 → processed_events가 멱등성 보장
      ack.acknowledge();

    } catch (Exception e) {
      log.error("[Consumer] 처리 실패 offset={}", record.offset(), e);
      // Phase 3에서 retry topic / DLQ 추가 예정
      ack.acknowledge();
    }
  }
}