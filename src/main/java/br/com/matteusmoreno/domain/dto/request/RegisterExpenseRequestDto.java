package br.com.matteusmoreno.domain.dto.request;

import br.com.matteusmoreno.domain.constant.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterExpenseRequestDto(
        @NotBlank(message = "Expense ID is required")
        String expenseId,

        @NotNull(message = "Payment method is required")
        PaymentMethod method
) {
}
