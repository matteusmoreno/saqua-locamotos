package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum CloudinaryFolder {

    USER_PICTURE("users/pictures"),
    USER_CONTRACT("users/contracts"),
    CONTRACT_FILE("contracts/files"),
    MOTORCYCLE_DOCUMENT("motorcycles/documents");

    private final String path;

    CloudinaryFolder(String path) {
        this.path = path;
    }
}

