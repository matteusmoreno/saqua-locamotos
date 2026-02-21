package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.exception.MotorcycleDocumentNotFoundException;
import br.com.matteusmoreno.application.service.CloudinaryService;
import br.com.matteusmoreno.domain.constant.CloudinaryFolder;
import br.com.matteusmoreno.domain.dto.request.UpdateMotorcycleRequestDto;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.repository.MotorcycleRepository;
import br.com.matteusmoreno.domain.dto.request.CreateMotorcycleRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;
    private final CloudinaryService cloudinaryService;

    public MotorcycleService(MotorcycleRepository motorcycleRepository, CloudinaryService cloudinaryService) {
        this.motorcycleRepository = motorcycleRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Motorcycle createMotorcycle(CreateMotorcycleRequestDto request) {
        Boolean motorcycleExists = this.motorcycleRepository.existsByRenavamOrPlateOrChassis(request.renavam(), request.plate(), request.chassis());

        if (motorcycleExists) {
            log.warn("Motorcycle with renavam {}, plate {} or chassis {} already exists", request.renavam(), request.plate(), request.chassis());
            return this.motorcycleRepository.findMotorcycleByRenavamOrPlateOrChassis(request.renavam(), request.plate(), request.chassis());
        }

        log.info("Creating motorcycle {}", request);
        Motorcycle motorcycle = Motorcycle.builder()
                .renavam(request.renavam().toUpperCase())
                .brand(request.brand().toUpperCase())
                .model(request.model().toUpperCase())
                .plate(request.plate().toUpperCase())
                .year(request.year().toUpperCase())
                .color(request.color().toUpperCase())
                .chassis(request.chassis().toUpperCase())
                .available(request.available())
                .active(true)
                .build();

        this.motorcycleRepository.persist(motorcycle);
        log.info("Created motorcycle {}", motorcycle);

        return motorcycle;
    }

    public Motorcycle findMotorcycleById(String motorcycleId) {
        log.info("Finding motorcycle {}", motorcycleId);
        return this.motorcycleRepository.findMotorcycleById(motorcycleId);
    }

    public List<Motorcycle> findAllMotorcycles() {
        log.info("Finding all motorcycles");
        return this.motorcycleRepository.listAll();
    }

    public List<Motorcycle> findAllAvailableMotorcycles() {
        log.info("Finding all available motorcycles");
        return this.motorcycleRepository.findAllAvailableMotorcycles();
    }

    public Motorcycle updateMotorcycle(UpdateMotorcycleRequestDto request) {
        log.info("Updating motorcycle {}", request);
        Motorcycle motorcycle = this.findMotorcycleById(request.motorcycleId());

        if (request.renavam() != null) motorcycle.setRenavam(request.renavam().toUpperCase());
        if (request.brand() != null) motorcycle.setBrand(request.brand().toUpperCase());
        if (request.model() != null) motorcycle.setModel(request.model().toUpperCase());
        if (request.plate() != null) motorcycle.setPlate(request.plate().toUpperCase());
        if (request.year() != null) motorcycle.setYear(request.year());
        if (request.color() != null) motorcycle.setColor(request.color().toUpperCase());
        if (request.chassis() != null) motorcycle.setChassis(request.chassis().toUpperCase());
        if (request.available() != null) motorcycle.setAvailable(request.available());

        this.motorcycleRepository.update(motorcycle);

        return motorcycle;
    }

    public void setMotorcycleAvailability(String motorcycleId, Boolean available) {
        log.info("Setting motorcycle {} availability to {}", motorcycleId, available);
        Motorcycle motorcycle = this.findMotorcycleById(motorcycleId);
        motorcycle.setAvailable(available);

        this.motorcycleRepository.update(motorcycle);
        log.info("Set motorcycle {} availability to {}", motorcycleId, available);
    }

    public Motorcycle disableMotorcycle(String motorcycleId) {
        log.info("Disabling motorcycle {}", motorcycleId);
        Motorcycle motorcycle = this.findMotorcycleById(motorcycleId);

        motorcycle.setActive(false);
        motorcycle.setAvailable(false);
        this.motorcycleRepository.update(motorcycle);

        log.info("Motorcycle {} disabled", motorcycleId);
        return motorcycle;
    }

    public Motorcycle enableMotorcycle(String motorcycleId) {
        log.info("Enabling motorcycle {}", motorcycleId);
        Motorcycle motorcycle = this.findMotorcycleById(motorcycleId);

        motorcycle.setActive(true);
        this.motorcycleRepository.update(motorcycle);

        log.info("Motorcycle {} enabled", motorcycleId);
        return motorcycle;
    }

    public Motorcycle uploadDocument(String motorcycleId, byte[] fileBytes) {
        Motorcycle motorcycle = this.findMotorcycleById(motorcycleId);

        if (motorcycle.getDocumentUrl() != null && !motorcycle.getDocumentUrl().isBlank()) {
            String oldPublicId = cloudinaryService.extractPublicId(motorcycle.getDocumentUrl());
            cloudinaryService.delete(oldPublicId);
        }

        String url = cloudinaryService.upload(fileBytes, motorcycle.getMotorcycleId(), CloudinaryFolder.MOTORCYCLE_DOCUMENT);
        motorcycle.setDocumentUrl(url);
        motorcycleRepository.update(motorcycle);

        log.info("Document uploaded for motorcycle: {}", motorcycleId);
        return motorcycle;
    }

    public Motorcycle deleteDocument(String motorcycleId) {
        Motorcycle motorcycle = this.findMotorcycleById(motorcycleId);

        if (motorcycle.getDocumentUrl() == null || motorcycle.getDocumentUrl().isBlank()) {
            throw new MotorcycleDocumentNotFoundException();
        }

        cloudinaryService.delete(cloudinaryService.extractPublicId(motorcycle.getDocumentUrl()));
        motorcycle.setDocumentUrl(null);
        motorcycleRepository.update(motorcycle);

        log.info("Document deleted for motorcycle: {}", motorcycleId);
        return motorcycle;
    }

}
