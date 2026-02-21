package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum ContractStatus {
    ACTIVE("Active"),
    FINISHED("Finished"),
    CANCELLED("Cancelled"),
    OVERDUE("Overdue");

    private final String displayName;

    ContractStatus(String displayName) {
        this.displayName = displayName;
    }
}

