package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum RentalType {
    MONTHLY("Monthly - weekly payments"),
    FIFTEEN_DAYS("15 days - full payment upfront");

    private final String displayName;

    RentalType(String displayName) {
        this.displayName = displayName;
    }
}

