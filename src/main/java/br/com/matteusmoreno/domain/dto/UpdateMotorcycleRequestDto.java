package br.com.matteusmoreno.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMotorcycleRequestDto(
            @NotBlank(message = "Motorcycle ID is required")
            String motorcycleId,
            String renavam,
            String brand,
            String model,
            String plate,
            String year,
            String color,
            String chassis,
            Boolean available
) {
}
