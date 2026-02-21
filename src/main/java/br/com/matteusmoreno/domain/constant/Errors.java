package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    MOTORCYCLE_NOT_FOUND("Motorcycle not found");


    private final String displayName;

    Errors(String displayName) {
        this.displayName = displayName;
    }

}
