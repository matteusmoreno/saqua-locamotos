package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.constant.ExpenseType;
import br.com.matteusmoreno.domain.constant.PaymentMethod;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponseDto(
        String expenseId,
        String motorcycleId,
        ExpenseType type,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDate paidDate,
        PaymentStatus status,
        PaymentMethod method,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ExpenseResponseDto(Expense expense) {
        this(
                expense.getExpenseId(),
                expense.getMotorcycleId(),
                expense.getType(),
                expense.getAmount(),
                expense.getDueDate(),
                expense.getPaidDate(),
                expense.getStatus(),
                expense.getMethod(),
                expense.getDescription(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}

