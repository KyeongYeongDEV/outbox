# ── 빌드 스테이지 ──────────────────────────────
FROM gradle:8.10-jdk21 AS builder
WORKDIR /workspace

COPY settings.gradle build.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY coupon-service/build.gradle ./coupon-service/
COPY coupon-service/src ./coupon-service/src
COPY payment-service/build.gradle ./payment-service/

RUN gradle :coupon-service:bootJar --no-daemon

# ── 실행 스테이지 ──────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/coupon-service/build/libs/coupon-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
