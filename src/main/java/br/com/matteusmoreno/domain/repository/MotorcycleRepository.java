package br.com.matteusmoreno.domain.repository;

import br.com.matteusmoreno.domain.entity.Motorcycle;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MotorcycleRepository implements PanacheMongoRepositoryBase<Motorcycle, String> {

    public Boolean existsByRenavamOrPlateOrChassis(String renavam, String plate, String chassis) {
        return this.find("renavam = ?1 or plate = ?2 or chassis = ?3", renavam, plate, chassis)
                .firstResultOptional()
                .isPresent();
    }

    public Motorcycle findMotorcycleByRenavamOrPlateOrChassis(String renavam, String plate, String chassis) {
        return this.find("renavam = ?1 or plate = ?2 or chassis = ?3", renavam, plate, chassis)
                .firstResult();
    }
}