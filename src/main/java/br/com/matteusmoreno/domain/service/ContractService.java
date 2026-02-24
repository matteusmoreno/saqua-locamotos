package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.service.CloudinaryService;
import br.com.matteusmoreno.application.service.PdfContractService;
import br.com.matteusmoreno.application.utils.DateUtils;
import br.com.matteusmoreno.domain.constant.CloudinaryFolder;
import br.com.matteusmoreno.domain.constant.ContractStatus;
import br.com.matteusmoreno.domain.constant.RentalType;
import br.com.matteusmoreno.domain.dto.request.CreateContractRequestDto;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.repository.ContractRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Slf4j
public class ContractService {

    private final ContractRepository contractRepository;
    private final UserService userService;
    private final MotorcycleService motorcycleService;
    private final CloudinaryService cloudinaryService;
    private final PdfContractService pdfContractService;
    private final DateUtils dateUtils;

    public ContractService(ContractRepository contractRepository, UserService userService, MotorcycleService motorcycleService, CloudinaryService cloudinaryService, PdfContractService pdfContractService, DateUtils dateUtils) {
        this.contractRepository = contractRepository;
        this.userService = userService;
        this.motorcycleService = motorcycleService;
        this.cloudinaryService = cloudinaryService;
        this.pdfContractService = pdfContractService;
        this.dateUtils = dateUtils;
    }

    public Contract createContract(CreateContractRequestDto request) {
        User user = this.userService.findUserById(request.userId());
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(request.motorcycleId());
        LocalDateTime now = this.dateUtils.now();

        this.motorcycleService.validateMotorcycleAvailability(motorcycle);

        LocalDate startDate = request.startDate();
        LocalDate endDate = request.rentalType() == RentalType.MONTHLY
                ? startDate.plusDays(30)
                : startDate.plusDays(15);

        Contract contract = Contract.builder()
                .user(user)
                .motorcycle(motorcycle)
                .rentalType(request.rentalType())
                .status(ContractStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate)
                .depositAmount(request.depositAmount())
                .depositPaid(false)
                .depositRefunded(false)
                .weeklyAmount(request.weeklyAmount())
                .totalAmount(BigDecimal.ZERO)
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.contractRepository.persist(contract);
        this.motorcycleService.setMotorcycleAvailability(motorcycle.getMotorcycleId(), false);

        log.info("Contract created with ID: {} for user: {} and motorcycle: {}",
                contract.getContractId(), user.getUserId(), motorcycle.getMotorcycleId());

        return contract;
    }

    public List<Contract> findAllContracts() {
        log.info("Listing all contracts");
        return this.contractRepository.listAll();
    }

    public Contract findContractById(String contractId) {
        log.info("Finding contract with ID: {}", contractId);
        return this.contractRepository.findContractById(contractId);
    }

    public List<Contract> findContractsByUserId(String userId) {
        log.info("Finding contracts for user: {}", userId);
        return this.contractRepository.findContractsByUserId(userId);
    }

    public List<Contract> findContractsByMotorcycleId(String motorcycleId) {
        log.info("Finding contracts for motorcycle: {}", motorcycleId);
        return this.contractRepository.findContractsByMotorcycleId(motorcycleId);
    }

    public Contract finishContract(String contractId, Boolean refundDeposit) {
        Contract contract = this.contractRepository.findContractById(contractId);
        LocalDateTime now = this.dateUtils.now();

        contract.setStatus(ContractStatus.FINISHED);
        contract.setDepositRefunded(refundDeposit);
        contract.setUpdatedAt(now);
        this.contractRepository.update(contract);

        this.motorcycleService.setMotorcycleAvailability(contract.getMotorcycle().getMotorcycleId(), true);

        log.info("Contract {} finished. Deposit refunded: {}", contractId, refundDeposit);
        return contract;
    }

    public Contract uploadContractFile(String contractId, byte[] fileBytes) {
        Contract contract = this.contractRepository.findContractById(contractId);
        LocalDateTime now = this.dateUtils.now();

        if (contract.getContractUrl() != null && !contract.getContractUrl().isBlank()) {
            this.cloudinaryService.delete(this.cloudinaryService.extractPublicId(contract.getContractUrl()), CloudinaryFolder.CONTRACT_FILE.getResourceType());
        }

        String url = this.cloudinaryService.upload(fileBytes, contract.getContractId(), CloudinaryFolder.CONTRACT_FILE);
        contract.setContractUrl(url);
        contract.setUpdatedAt(now);
        this.contractRepository.update(contract);

        log.info("Contract file uploaded for contract: {}", contractId);
        return contract;
    }

    public byte[] generateContractPdf(String contractId) {
        Contract contract = this.contractRepository.findContractById(contractId);
        log.info("Generating PDF for contract: {}", contractId);
        return this.pdfContractService.generateContractPdf(contract);
    }

    public Contract cancelContract(String contractId) {
        Contract contract = this.contractRepository.findContractById(contractId);
        LocalDateTime now = this.dateUtils.now();

        contract.setStatus(ContractStatus.CANCELLED);
        contract.setUpdatedAt(now);
        this.contractRepository.update(contract);

        this.motorcycleService.setMotorcycleAvailability(contract.getMotorcycle().getMotorcycleId(), true);

        log.info("Contract {} cancelled", contractId);
        return contract;
    }
}
