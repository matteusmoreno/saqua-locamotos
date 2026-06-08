package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum ExpenseType {
    ACQUISITION ("Acquisition"),
    MAINTENANCE("Maintenance"),
    UTILITIES("Utilities"),
    TAXES("Taxes"),
    INSURANCE("Insurance"),
    OTHER("Other");

    private final String displayName;

    ExpenseType(String displayName) {
        this.displayName = displayName;
    }

}
