package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.constant.PaymentMethod;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.constant.PaymentType;
import br.com.matteusmoreno.domain.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponseDto(
        String paymentId,
        String contractId,
        PaymentType type,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDate paidDate,
        PaymentStatus status,
        PaymentMethod method,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public PaymentResponseDto(Payment payment) {
        this(
                payment.getPaymentId(),
                payment.getContractId(),
                payment.getType(),
                payment.getAmount(),
                payment.getDueDate(),
                payment.getPaidDate(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getDescription(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
