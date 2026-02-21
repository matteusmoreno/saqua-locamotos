package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.CreateMotorcycleRequestDto;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.service.MotorcycleService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MotorcycleController {

    private final MotorcycleService motorcycleService;

    public MotorcycleController(MotorcycleService motorcycleService) {
        this.motorcycleService = motorcycleService;
    }

    public Motorcycle createMotorcycle(CreateMotorcycleRequestDto request) {
        return this.motorcycleService.createMotorcycle(request);
    }

    public Motorcycle findMotorcycleById(String motorcycleId) {
        return this.motorcycleService.findMotorcycleById(motorcycleId);
    }
}
