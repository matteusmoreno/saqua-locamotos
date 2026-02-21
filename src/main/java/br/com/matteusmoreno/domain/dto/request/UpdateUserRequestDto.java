package br.com.matteusmoreno.domain.dto.request;

import br.com.matteusmoreno.domain.constant.MaritalStatus;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequestDto(
        @NotBlank(message = "User ID is required")
        String userId,
        String name,
        String email,
        String phone,
        String cpf,
        String rg,
        String ocupation,
        MaritalStatus maritalStatus,
        CreateAddressRequestDto address
        ) {
}
