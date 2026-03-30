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
        String documentUrl,
        String pictureUrl,
        Integer mileage,
        Boolean available,
        Boolean active,
        FinancialResponseDto financial
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
                motorcycle.getDocumentUrl(),
                motorcycle.getPictureUrl(),
                motorcycle.getMileage(),
                motorcycle.getAvailable(),
                motorcycle.getActive(),
                new FinancialResponseDto(motorcycle.getFinancial())
        );
    }
}
