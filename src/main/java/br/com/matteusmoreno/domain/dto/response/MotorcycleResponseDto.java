package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.entity.Motorcycle;

public record MotorcycleResponseDto(
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

    public MotorcycleResponseDto(Motorcycle motorcycle) {
        this(
                motorcycle.getMotorcycleId(),
                motorcycle.getRenavam(),
                motorcycle.getBrand(),
                motorcycle.getModel(),
                motorcycle.getPlate(),
                motorcycle.getYear(),
                motorcycle.getColor(),
                motorcycle.getChassis(),
                motorcycle.getAvailable()
        );
    }
}
