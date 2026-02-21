package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.exception.FineNotFoundException;
import br.com.matteusmoreno.application.exception.PaymentNotFoundException;
import br.com.matteusmoreno.application.service.CloudinaryService;
import br.com.matteusmoreno.domain.constant.CloudinaryFolder;
import br.com.matteusmoreno.domain.constant.ContractStatus;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.constant.RentalType;
import br.com.matteusmoreno.domain.dto.request.AddFineRequestDto;
import br.com.matteusmoreno.domain.dto.request.CreateContractRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterPaymentRequestDto;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.model.Fine;
import br.com.matteusmoreno.domain.model.Payment;
import br.com.matteusmoreno.domain.repository.ContractRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class ContractService {

    private final ContractRepository contractRepository;
    private final UserService userService;
    private final MotorcycleService motorcycleService;
    private final CloudinaryService cloudinaryService;

    public ContractService(ContractRepository contractRepository, UserService userService, MotorcycleService motorcycleService, CloudinaryService cloudinaryService) {
        this.contractRepository = contractRepository;
        this.userService = userService;
        this.motorcycleService = motorcycleService;
        this.cloudinaryService = cloudinaryService;
    }

    public Contract createContract(CreateContractRequestDto request) {
        User user = userService.findUserById(request.userId());
        Motorcycle motorcycle = motorcycleService.findMotorcycleById(request.motorcycleId());

        motorcycleService.validateMotorcycleAvailability(motorcycle);

        LocalDate startDate = request.startDate();
        LocalDate endDate;
        List<Payment> payments = new ArrayList<>();

        if (request.rentalType() == RentalType.MONTHLY) {
            endDate = startDate.plusDays(30);
            // Generate 4 weekly payments
            for (int i = 1; i <= 4; i++) {
                payments.add(Payment.builder()
                        .amount(request.weeklyAmount())
                        .dueDate(startDate.plusWeeks(i))
                        .status(PaymentStatus.PENDING)
                        .description("Weekly payment " + i + "/4")
                        .build());
            }
        } else {
            // FIFTEEN_DAYS - full payment upfront
            endDate = startDate.plusDays(15);
            payments.add(Payment.builder()
                    .amount(request.weeklyAmount())
                    .dueDate(startDate)
                    .status(PaymentStatus.PENDING)
                    .description("Full payment - 15 days rental")
                    .build());
        }

        BigDecimal totalAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(request.depositAmount());

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
                .totalAmount(totalAmount)
                .payments(payments)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        contractRepository.persist(contract);
        motorcycleService.setMotorcycleAvailability(motorcycle.getMotorcycleId(), false);

        log.info("Contract created with ID: {} for user: {} and motorcycle: {}",
                contract.getContractId(), user.getUserId(), motorcycle.getMotorcycleId());

        return contract;
    }

    public Contract findContractById(String contractId) {
        log.info("Finding contract with ID: {}", contractId);
        return contractRepository.findContractById(contractId);
    }

    public List<Contract> findContractsByUserId(String userId) {
        log.info("Finding contracts for user: {}", userId);
        return contractRepository.findContractsByUserId(userId);
    }

    public List<Contract> findContractsByMotorcycleId(String motorcycleId) {
        log.info("Finding contracts for motorcycle: {}", motorcycleId);
        return contractRepository.findContractsByMotorcycleId(motorcycleId);
    }

    public Contract registerPayment(RegisterPaymentRequestDto request) {
        Contract contract = contractRepository.findContractById(request.contractId());

        Payment payment = contract.getPayments().stream()
                .filter(p -> p.getPaymentId().equals(request.paymentId()))
                .findFirst()
                .orElseThrow(PaymentNotFoundException::new);

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidDate(LocalDate.now());
        payment.setMethod(request.method());

        // if deposit payment description, mark deposit as paid
        if (Boolean.FALSE.equals(contract.getDepositPaid()) && payment.getDescription() != null
                && payment.getDescription().toLowerCase().contains("deposit")) {
            contract.setDepositPaid(true);
        }

        // if all payments are PAID and no unpaid fines, mark contract as ACTIVE
        boolean allPaymentsPaid = contract.getPayments().stream().allMatch(p -> p.getStatus() == PaymentStatus.PAID);
        boolean hasUnpaidFines = contract.getFines().stream().anyMatch(f -> Boolean.FALSE.equals(f.getPaid()));
        if (allPaymentsPaid && !hasUnpaidFines) {
            contract.setStatus(ContractStatus.ACTIVE);
        }

        contract.setUpdatedAt(LocalDateTime.now());
        contractRepository.update(contract);

        log.info("Payment {} registered for contract {}", request.paymentId(), request.contractId());
        return contract;
    }

    public Contract addFine(AddFineRequestDto request) {
        Contract contract = contractRepository.findContractById(request.contractId());

        Fine fine = Fine.builder()
                .amount(request.amount())
                .reason(request.reason())
                .createdAt(LocalDateTime.now())
                .paid(false)
                .build();

        contract.getFines().add(fine);
        contract.setStatus(ContractStatus.OVERDUE);
        contract.setUpdatedAt(LocalDateTime.now());
        contractRepository.update(contract);

        log.info("Fine added to contract {}: {} - {}", request.contractId(), request.amount(), request.reason());
        return contract;
    }

    public Contract payFine(String contractId, String fineId) {
        Contract contract = contractRepository.findContractById(contractId);

        Fine fine = contract.getFines().stream()
                .filter(f -> f.getFineId().equals(fineId))
                .findFirst()
                .orElseThrow(FineNotFoundException::new);

        fine.setPaid(true);
        fine.setPaidAt(LocalDateTime.now());

        boolean allFinesPaid = contract.getFines().stream().allMatch(Fine::getPaid);
        if (allFinesPaid) contract.setStatus(ContractStatus.ACTIVE);

        contract.setUpdatedAt(LocalDateTime.now());
        contractRepository.update(contract);

        log.info("Fine {} paid for contract {}", fineId, contractId);
        return contract;
    }

    public Contract finishContract(String contractId, Boolean refundDeposit) {
        Contract contract = contractRepository.findContractById(contractId);

        contract.setStatus(ContractStatus.FINISHED);
        contract.setDepositRefunded(refundDeposit);
        contract.setUpdatedAt(LocalDateTime.now());
        contractRepository.update(contract);

        motorcycleService.setMotorcycleAvailability(contract.getMotorcycle().getMotorcycleId(), true);

        log.info("Contract {} finished. Deposit refunded: {}", contractId, refundDeposit);
        return contract;
    }

    public Contract uploadContractFile(String contractId, byte[] fileBytes) {
        Contract contract = contractRepository.findContractById(contractId);

        if (contract.getContractUrl() != null && !contract.getContractUrl().isBlank()) {
            cloudinaryService.delete(cloudinaryService.extractPublicId(contract.getContractUrl()));
        }

        String url = cloudinaryService.upload(fileBytes, contract.getContractId(), CloudinaryFolder.CONTRACT_FILE);
        contract.setContractUrl(url);
        contract.setUpdatedAt(LocalDateTime.now());
        contractRepository.update(contract);

        log.info("Contract file uploaded for contract: {}", contractId);
        return contract;
    }

    public Contract cancelContract(String contractId) {
        Contract contract = contractRepository.findContractById(contractId);

        contract.setStatus(ContractStatus.CANCELLED);
        contract.setUpdatedAt(LocalDateTime.now());
        contractRepository.update(contract);

        motorcycleService.setMotorcycleAvailability(contract.getMotorcycle().getMotorcycleId(), true);

        log.info("Contract {} cancelled", contractId);
        return contract;
    }
}

