CREATE TABLE payments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    amount     BIGINT      NOT NULL,
    currency   VARCHAR(10) NOT NULL DEFAULT 'KRW',
    status     VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB;

CREATE TABLE outbox (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    event_id       CHAR(36)     NOT NULL UNIQUE,
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