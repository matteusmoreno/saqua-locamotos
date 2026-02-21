package br.com.matteusmoreno.domain.dto.request;

import br.com.matteusmoreno.domain.constant.RentalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateContractRequestDto(

        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Motorcycle ID is required")
        String motorcycleId,

        @NotNull(message = "Rental type is required")
        RentalType rentalType,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "Deposit amount is required")
        @Positive(message = "Deposit amount must be positive")
        BigDecimal depositAmount,

        @NotNull(message = "Weekly amount is required")
        @Positive(message = "Weekly amount must be positive")
        BigDecimal weeklyAmount
) {}

