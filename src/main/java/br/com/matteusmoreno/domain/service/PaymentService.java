package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.utils.DateUtils;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.dto.request.CreatePaymentRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterPaymentRequestDto;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.entity.Payment;
import br.com.matteusmoreno.domain.repository.ContractRepository;
import br.com.matteusmoreno.domain.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final MotorcycleService motorcycleService;
    private final FinancialService financialService;
    private final DateUtils dateUtils;

    public PaymentService(PaymentRepository paymentRepository, ContractRepository contractRepository, MotorcycleService motorcycleService, FinancialService financialService, DateUtils dateUtils) {
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
        this.motorcycleService = motorcycleService;
        this.financialService = financialService;
        this.dateUtils = dateUtils;
    }

    public Payment createPayment(CreatePaymentRequestDto request) {
        log.info("Creating payment with data: {}", request);
        Contract contract = this.contractRepository.findContractById(request.contractId());
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(contract.getMotorcycle().getMotorcycleId());
        LocalDateTime now = this.dateUtils.now();

        Payment payment = Payment.builder()
                .contractId(request.contractId())
                .type(request.type())
                .amount(request.amount())
                .dueDate(request.dueDate())
                .status(PaymentStatus.PENDING)
                .description(request.description())
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.paymentRepository.persist(payment);
        this.financialService.saveEarning(motorcycle, payment);

        contract.getPayments().add(payment);
        this.contractRepository.update(contract);

        log.info("Payment created with ID: {} for contract: {}", payment.getPaymentId(), request.contractId());
        return payment;
    }

    public Payment findPaymentById(String paymentId) {
        log.info("Finding payment with ID: {}", paymentId);
        return this.paymentRepository.findPaymentById(paymentId);
    }

    public List<Payment> findPaymentsByContractId(String contractId) {
        log.info("Finding payments for contract: {}", contractId);
        return this.paymentRepository.findPaymentsByContractId(contractId);
    }

    public Payment registerPayment(RegisterPaymentRequestDto request) {
        log.info("Register payment request: {}", request);
        Payment payment = this.paymentRepository.findPaymentById(request.paymentId());
        LocalDateTime now = this.dateUtils.now();
        LocalDate today = this.dateUtils.today();

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidDate(today);
        payment.setMethod(request.method());
        payment.setUpdatedAt(now);

        this.paymentRepository.update(payment);

        Contract contract = this.contractRepository.findContractById(payment.getContractId());
        contract.setTotalAmount(contract.getTotalAmount().add(payment.getAmount()));
        contract.setUpdatedAt(now);

        // Sincroniza o objeto payment na lista do contrato (status PAID)
        contract.getPayments().replaceAll(p -> p.getPaymentId().equals(payment.getPaymentId()) ? payment : p);
        this.contractRepository.update(contract);

        // Sincroniza o Financial da moto com o payment atualizado (status PAID)
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(contract.getMotorcycle().getMotorcycleId());
        this.financialService.updateEarning(motorcycle, payment);

        log.info("Payment {} registered as PAID", request.paymentId());
        return payment;
    }

    public Payment deletePayment(String paymentId) {
        log.info("Deleting payment with ID: {}", paymentId);
        Payment payment = this.paymentRepository.findPaymentById(paymentId);
        Contract contract = this.contractRepository.findContractById(payment.getContractId());
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(contract.getMotorcycle().getMotorcycleId());

        contract.getPayments().removeIf(p -> p.getPaymentId().equals(paymentId));
        this.contractRepository.update(contract);

        this.paymentRepository.delete(payment);
        this.financialService.removeEarning(motorcycle, paymentId);

        log.info("Payment {} deleted from contract {}", paymentId, payment.getContractId());
        return payment;
    }
}

