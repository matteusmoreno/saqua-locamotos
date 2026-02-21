package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.model.Fine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineResponseDto(
        String fineId,
        BigDecimal amount,
        String reason,
        LocalDateTime createdAt,
        Boolean paid,
        LocalDateTime paidAt
) {
    public FineResponseDto(Fine fine) {
        this(
                fine.getFineId(),
                fine.getAmount(),
                fine.getReason(),
                fine.getCreatedAt(),
                fine.getPaid(),
                fine.getPaidAt()
        );
    }
}

