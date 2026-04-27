# ── 빌드 스테이지 ──────────────────────────────
FROM gradle:8.10-jdk21 AS builder
WORKDIR /workspace

# 멀티모듈 전체 컨텍스트를 복사해야 루트 build.gradle을 인식
COPY settings.gradle build.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY payment-service/build.gradle ./payment-service/
COPY payment-service/src ./payment-service/src

# coupon-service도 settings.gradle에 등록돼있어서 최소한 build.gradle은 있어야 함
COPY coupon-service/build.gradle ./coupon-service/

RUN gradle :payment-service:bootJar --no-daemon

# ── 실행 스테이지 ──────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/payment-service/build/libs/payment-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
