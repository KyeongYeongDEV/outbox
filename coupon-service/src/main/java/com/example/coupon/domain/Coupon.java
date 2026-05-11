package com.example.coupon.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long paymentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DiscountType discountType;

  @Column(nullable = false)
  private Long discountValue;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CouponStatus status;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  private Coupon(Long userId, Long paymentId, DiscountType discountType, Long discountValue) {
    this.userId = userId;
    this.paymentId = paymentId;
    this.discountType = discountType;
    this.discountValue = discountValue;
    this.status = CouponStatus.ACTIVE;
    this.createdAt = LocalDateTime.now();
    this.expiredAt = LocalDateTime.now().plusDays(30);
  }

  public static Coupon create(Long userId, Long paymentId,
      DiscountType discountType, Long discountValue) {
    return new Coupon(userId, paymentId, discountType, discountValue);
  }
}