package br.com.matteusmoreno.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMotorcycleRequestDto(
        @NotBlank(message = "Renavam is required")
        String renavam,
        @NotBlank(message = "Brand is required")
        String brand,
        @NotBlank(message = "Model is required")
        String model,
        @NotBlank(message = "Plate is required")
        String plate,
        @NotBlank(message = "Year is required")
        String year,
        @NotBlank(message = "Color is required")
        String color,
        @NotBlank(message = "Chassis is required")
        String chassis,
        @NotNull(message = "Available is required")
        Boolean available
) {
}
