package com.example.coupon.infrastructure.messaging;

import com.example.coupon.infrastructure.outbox.OutboxMessage;
import com.example.coupon.infrastructure.outbox.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "outbox.polling.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPollingPublisher {

  private static final int BATCH_SIZE = 100;
  private static final int MAX_RETRY = 5;

  private final OutboxMessageRepository outboxRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Scheduled(fixedDelay = 1000)
  @Transactional
  public void publish() {
    List<OutboxMessage> messages = outboxRepository.findPendingMessages(BATCH_SIZE);
    if (messages.isEmpty())
      return;

    for (OutboxMessage msg : messages) {
      try {
        String topic = msg.getAggregateType().toLowerCase() + "." + msg.getEventType();

        kafkaTemplate.send(topic, msg.getAggregateId(), msg.getPayload()).get();
        msg.markPublished();
        log.info("[Polling] 발행 완료 eventId={} topic={}", msg.getEventId(), topic);

      } catch (Exception e) {
        log.error("[Polling] 발행 실패 eventId={}", msg.getEventId(), e);
        handleFailure(msg);
      }
    }
  }

  private void handleFailure(OutboxMessage msg) {
    msg.incrementRetry();
    if (msg.getRetryCount() >= MAX_RETRY) {
      msg.markFailed();
      log.error("[Polling] FAILED 처리 eventId={} retryCount={}", msg.getEventId(), msg.getRetryCount());
    }
  }
}
