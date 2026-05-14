CREATE TABLE coupons (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    payment_id     BIGINT      NOT NULL,
    discount_type  VARCHAR(20) NOT NULL,
    discount_value BIGINT      NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at     DATETIME(6) NOT NULL,
    expired_at     DATETIME(6) NOT NULL,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB;

CREATE TABLE outbox (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    event_id       VARCHAR(36)     NOT NULL UNIQUE,
    aggregate_type VARCHAR(50)  NOT NULL,
    aggregate_id   VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    payload        JSON         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retry_count    INT          NOT NULL DEFAULT 0,
    created_at     DATETIME(6)  NOT NULL,
    published_at   DATETIME(6)  NULL,
    INDEX idx_status_created (status, created_at)
) ENGINE=InnoDB;

-- Consumer 멱등성 보장
-- PK(event_id, consumer_group) 충돌 = 이미 처리된 이벤트 → 즉시 감지
CREATE TABLE processed_events (
    event_id       VARCHAR(36)     NOT NULL,
    consumer_group VARCHAR(100) NOT NULL,
    processed_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (event_id, consumer_group)
) ENGINE=InnoDB;