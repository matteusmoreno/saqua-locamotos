package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    MOTORCYCLE_NOT_FOUND("Motorcycle not found"),
    MOTORCYCLE_NOT_AVAILABLE("Motorcycle not available"),
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists");


    private final String displayName;

    Errors(String displayName) {
        this.displayName = displayName;
    }

}
