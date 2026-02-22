package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.constant.ContractStatus;
import br.com.matteusmoreno.domain.constant.RentalType;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.model.Fine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ContractResponseDto(
        String contractId,
        UserResponseDto user,
        MotorcycleResponseDto motorcycle,
        RentalType rentalType,
        ContractStatus status,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal depositAmount,
        Boolean depositPaid,
        Boolean depositRefunded,
        BigDecimal weeklyAmount,
        BigDecimal totalAmount,
        BigDecimal totalFines,
        String contractUrl,
        List<String> paymentIds,
        List<FineResponseDto> fines,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ContractResponseDto(Contract contract) {
        this(
                contract.getContractId(),
                new UserResponseDto(contract.getUser()),
                new MotorcycleResponseDto(contract.getMotorcycle()),
                contract.getRentalType(),
                contract.getStatus(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getDepositAmount(),
                contract.getDepositPaid(),
                contract.getDepositRefunded(),
                contract.getWeeklyAmount(),
                contract.getTotalAmount(),
                contract.getFines().stream()
                        .map(Fine::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                contract.getContractUrl(),
                contract.getPaymentIds(),
                contract.getFines().stream().map(FineResponseDto::new).toList(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }
}
