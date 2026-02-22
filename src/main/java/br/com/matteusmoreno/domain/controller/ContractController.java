package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.AddFineRequestDto;
import br.com.matteusmoreno.domain.dto.request.CreateContractRequestDto;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.service.ContractService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    public Contract createContract(CreateContractRequestDto request) {
        return contractService.createContract(request);
    }

    public List<Contract> findAllContracts() {
        return contractService.findAllContracts();
    }

    public Contract findContractById(String contractId) {
        return contractService.findContractById(contractId);
    }

    public List<Contract> findContractsByUserId(String userId) {
        return contractService.findContractsByUserId(userId);
    }

    public List<Contract> findContractsByMotorcycleId(String motorcycleId) {
        return contractService.findContractsByMotorcycleId(motorcycleId);
    }

    public Contract addFine(AddFineRequestDto request) {
        return contractService.addFine(request);
    }

    public Contract payFine(String contractId, String fineId) {
        return contractService.payFine(contractId, fineId);
    }

    public Contract finishContract(String contractId, Boolean refundDeposit) {
        return contractService.finishContract(contractId, refundDeposit);
    }

    public Contract cancelContract(String contractId) {
        return contractService.cancelContract(contractId);
    }

    public Contract uploadContractFile(String contractId, byte[] fileBytes) {
        return contractService.uploadContractFile(contractId, fileBytes);
    }
}
