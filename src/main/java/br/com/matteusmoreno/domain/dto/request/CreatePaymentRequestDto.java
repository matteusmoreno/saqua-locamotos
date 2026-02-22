package br.com.matteusmoreno.domain.dto.request;

import br.com.matteusmoreno.domain.constant.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePaymentRequestDto(

        @NotBlank(message = "Contract ID is required")
        String contractId,

        @NotNull(message = "Payment type is required")
        PaymentType type,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Due date is required")
        LocalDate dueDate,

        String description
) {}

