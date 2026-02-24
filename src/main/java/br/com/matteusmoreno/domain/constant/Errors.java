package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    MOTORCYCLE_NOT_FOUND("Motorcycle not found"),
    MOTORCYCLE_NOT_AVAILABLE("Motorcycle not available"),
    MOTORCYCLE_DOCUMENT_NOT_FOUND("Motorcycle does not have a document"),
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    PICTURE_NOT_FOUND("User does not have a picture"),
    DOCUMENT_NOT_FOUND("User does not have the specified document"),
    CONTRACT_NOT_FOUND("Contract not found"),
    PAYMENT_NOT_FOUND("Payment not found"),
    FINE_NOT_FOUND("Fine not found"),
    UNAUTHORIZED_ACCESS("Access denied");

    private final String displayName;

    Errors(String displayName) {
        this.displayName = displayName;
    }

}
