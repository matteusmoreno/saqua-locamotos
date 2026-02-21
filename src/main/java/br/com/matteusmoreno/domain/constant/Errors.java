package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    MOTORCYCLE_NOT_FOUND("Motorcycle not found"),
    MOTORCYCLE_NOT_AVAILABLE("Motorcycle not available"),
    MOTORCYCLE_NOT_ASSIGNED_TO_USER("Motorcycle not assigned to user"),
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    PICTURE_NOT_FOUND("User does not have a picture"),
    CONTRACT_NOT_FOUND("User does not have a contract");


    private final String displayName;

    Errors(String displayName) {
        this.displayName = displayName;
    }

}
