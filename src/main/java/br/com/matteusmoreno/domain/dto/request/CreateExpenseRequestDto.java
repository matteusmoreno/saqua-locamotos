package br.com.matteusmoreno.domain.dto.request;

import br.com.matteusmoreno.domain.constant.ExpenseType;
import br.com.matteusmoreno.domain.constant.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequestDto(

        @NotBlank(message = "Motorcycle ID is required")
        String motorcycleId,

        @NotNull(message = "Expense type is required")
        ExpenseType type,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        PaymentMethod method,

        String description,

        @NotNull(message = "Due date is required")
        LocalDate dueDate
) {
}
