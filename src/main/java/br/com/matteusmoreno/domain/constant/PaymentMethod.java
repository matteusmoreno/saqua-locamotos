package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    PIX("Pix"),
    CASH("Cash"),
    CARD("Card");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}

