package com.example.payment.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Long> {

  // FOR UPDATE SKIP LOCKED:
  // 다중 인스턴스 환경에서 다른 인스턴스가 처리 중인 row는 건너뜀
  // 같은 메시지를 두 인스턴스가 동시에 처리하는 것을 DB 레벨에서 방지
  @Query(value = """
      SELECT * FROM outbox
      WHERE status = 'PENDING'
      ORDER BY created_at ASC
      LIMIT :limit
      FOR UPDATE SKIP LOCKED
      """, nativeQuery = true)
  List<OutboxMessage> findPendingMessages(int limit);
}