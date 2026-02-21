package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Admin"),
    CUSTOMER("Customer");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}
