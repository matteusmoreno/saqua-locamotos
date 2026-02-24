package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum PaymentType {
    DEPOSIT("Deposit / Caução"),
    WEEKLY("Weekly Payment"),
    FULL_PAYMENT("Full Payment - 15 days"),
    FINE("Fine / Multa");

    private final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }
}
