package br.com.matteusmoreno.domain.constant;

import lombok.Getter;

@Getter
public enum CloudinaryFolder {

    USER_PICTURE("users/pictures", "image"),
    USER_DOCUMENT("users/documents", "raw"),
    CONTRACT_FILE("contracts/files", "raw"),
    MOTORCYCLE_DOCUMENT("motorcycles/documents", "raw");


    private final String path;
    private final String resourceType;

    CloudinaryFolder(String path, String resourceType) {
        this.path = path;
        this.resourceType = resourceType;
    }
}


