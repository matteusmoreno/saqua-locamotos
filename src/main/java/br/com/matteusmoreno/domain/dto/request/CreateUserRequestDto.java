package br.com.matteusmoreno.domain.dto.request;

import br.com.matteusmoreno.domain.constant.MaritalStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequestDto(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "\\(\\d{2}\\)\\d{5}-\\d{4}", message = "Phone must be in the format (XX)XXXXX-XXXX")
        String phone,
        @NotBlank(message = "CPF is required")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF must be in the format XXX.XXX.XXX-XX")
        String cpf,
        @NotBlank(message = "RG is required")
        String rg,
        String occupation,
        MaritalStatus maritalStatus,
        CreateAddressRequestDto address,
        String pictureUrl
) {
}
