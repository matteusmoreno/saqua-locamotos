package br.com.matteusmoreno.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequestDto(
        @NotBlank(message = "User ID is required")
        String userId,
        @NotBlank(message = "Current password is required")
        String currentPassword,
        @NotBlank(message = "New password is required")
        String newPassword
) {
}
