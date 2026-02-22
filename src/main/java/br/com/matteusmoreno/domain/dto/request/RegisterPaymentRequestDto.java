package br.com.matteusmoreno.domain.dto.request;

import br.com.matteusmoreno.domain.constant.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterPaymentRequestDto(

        @NotBlank(message = "Payment ID is required")
        String paymentId,

        @NotNull(message = "Payment method is required")
        PaymentMethod method
) {}
