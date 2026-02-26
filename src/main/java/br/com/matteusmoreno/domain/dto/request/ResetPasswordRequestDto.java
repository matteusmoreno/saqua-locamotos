package br.com.matteusmoreno.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDto(

        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "New password is required")
        String newPassword
) {
}

