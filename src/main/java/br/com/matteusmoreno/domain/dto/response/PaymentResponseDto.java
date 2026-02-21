package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.constant.PaymentMethod;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponseDto(
        String paymentId,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDate paidDate,
        PaymentStatus status,
        PaymentMethod method,
        String description
) {
    public PaymentResponseDto(Payment payment) {
        this(
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getDueDate(),
                payment.getPaidDate(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getDescription()
        );
    }
}

