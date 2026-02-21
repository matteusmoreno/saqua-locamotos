package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.CreateMotorcycleRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdateMotorcycleRequestDto;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.service.MotorcycleService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

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

    public List<Motorcycle> findAllMotorcycles() {
        return this.motorcycleService.findAllMotorcycles();
    }

    public List<Motorcycle> findAllAvailableMotorcycles() {
        return this.motorcycleService.findAllAvailableMotorcycles();
    }

    public Motorcycle updateMotorcycle(UpdateMotorcycleRequestDto request) {
        return this.motorcycleService.updateMotorcycle(request);
    }

    public Motorcycle disableMotorcycle(String motorcycleId) {
        return this.motorcycleService.disableMotorcycle(motorcycleId);
    }

    public Motorcycle enableMotorcycle(String motorcycleId) {
        return this.motorcycleService.enableMotorcycle(motorcycleId);
    }

    public Motorcycle uploadDocument(String motorcycleId, byte[] fileBytes) {
        return this.motorcycleService.uploadDocument(motorcycleId, fileBytes);
    }

    public Motorcycle deleteDocument(String motorcycleId) {
        return this.motorcycleService.deleteDocument(motorcycleId);
    }
}
