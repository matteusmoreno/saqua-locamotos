package br.com.matteusmoreno.domain.model;

import br.com.matteusmoreno.domain.constant.PaymentMethod;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Payment {

    @Builder.Default
    private String paymentId = UUID.randomUUID().toString();
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private PaymentStatus status;
    private PaymentMethod method;
    private String description;
}

