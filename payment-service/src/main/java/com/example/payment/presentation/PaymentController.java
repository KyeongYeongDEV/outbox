package com.example.payment.presentation;

import com.example.payment.application.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest req) {
    Long paymentId = paymentService.pay(req.userId(), req.amount(), req.currency());
    return ResponseEntity.ok(new PaymentResponse(paymentId));
  }

  public record PaymentRequest(Long userId, Long amount, String currency) {
  }

  public record PaymentResponse(Long paymentId) {
  }
}