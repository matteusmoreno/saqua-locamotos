package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.model.Financial;

import java.math.BigDecimal;
import java.util.List;

public record FinancialResponseDto(
        List<PaymentResponseDto> earnings,
        List<ExpenseResponseDto> expenses,
        BigDecimal total
) {
    public FinancialResponseDto(Financial financial) {
        this(
                financial == null || financial.getEarnings() == null
                        ? List.of()
                        : financial.getEarnings().stream().map(PaymentResponseDto::new).toList(),
                financial == null || financial.getExpenses() == null
                        ? List.of()
                        : financial.getExpenses().stream().map(ExpenseResponseDto::new).toList(),
                financial == null ? BigDecimal.ZERO : financial.getTotal()
        );
    }
}

