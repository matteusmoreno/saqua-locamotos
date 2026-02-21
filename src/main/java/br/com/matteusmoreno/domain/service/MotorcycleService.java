package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.repository.MotorcycleRepository;
import br.com.matteusmoreno.domain.dto.CreateMotorcycleRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;

    public MotorcycleService(MotorcycleRepository motorcycleRepository) {
        this.motorcycleRepository = motorcycleRepository;
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
                .year(request.year())
                .color(request.color().toUpperCase())
                .chassis(request.chassis().toUpperCase())
                .available(request.available())
                .build();

        this.motorcycleRepository.persist(motorcycle);
        log.info("Created motorcycle {}", motorcycle);

        return motorcycle;
    }

}
