package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    MOTORCYCLE_NOT_FOUND("Motorcycle not found"),
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists");


    private final String displayName;

    Errors(String displayName) {
        this.displayName = displayName;
    }

}
