package br.com.matteusmoreno.domain.repository;

import br.com.matteusmoreno.application.exception.ContractNotFoundException;
import br.com.matteusmoreno.domain.entity.Contract;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class ContractRepository implements PanacheMongoRepositoryBase<Contract, String> {

    public Contract findContractById(String contractId) {
        if (!ObjectId.isValid(contractId)) throw new ContractNotFoundException();

        return find("_id", new ObjectId(contractId)).firstResultOptional()
                .orElseThrow(ContractNotFoundException::new);
    }

    public List<Contract> findContractsByUserId(String userId) {
        return find("user.userId", userId).list();
    }

    public List<Contract> findContractsByMotorcycleId(String motorcycleId) {
        return find("motorcycle.motorcycleId", motorcycleId).list();
    }
}

