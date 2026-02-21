package br.com.matteusmoreno.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Fine {

    @Builder.Default
    private String fineId = UUID.randomUUID().toString();
    private BigDecimal amount;
    private String reason;
    private LocalDateTime createdAt;
    private Boolean paid;
    private LocalDateTime paidAt;
}

