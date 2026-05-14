package com.example.coupon.infrastructure.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.example.coupon.application.CouponService;
import com.example.coupon.application.event.PaymentCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

  private final CouponService couponService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "outbox.event.Payment", groupId = "coupon-service")
  public void onPaymentCompleted(ConsumerRecord<String, String> record, Acknowledgment ack)
      throws Exception { // ← 추가: checked exception을 Spring Kafka로 위임

    log.info("[Consumer] 수신 topic={} partition={} offset={}",
        record.topic(), record.partition(), record.offset());

    // try-catch 제거 → 예외가 터지면 Spring Kafka가 재시도 → DLQ로 보냄
    // 기존처럼 catch 안에서 ack하면 DLQ가 동작 안 함
    PaymentCompletedEvent event = objectMapper.readValue(record.value(), PaymentCompletedEvent.class);

    couponService.issueFromPayment(event);

    // 성공 시에만 ack
    ack.acknowledge();
  }
}