package com.example.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long amount;

  @Column(nullable = false, length = 10)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private Payment(Long userId, Long amount, String currency) {
    this.userId = userId;
    this.amount = amount;
    this.currency = currency;
    this.status = PaymentStatus.COMPLETED;
    this.createdAt = LocalDateTime.now();
  }

  public static Payment create(Long userId, Long amount, String currency) {
    return new Payment(userId, amount, currency);
  }
}