package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.dto.request.CreatePaymentRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterPaymentRequestDto;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.entity.Payment;
import br.com.matteusmoreno.domain.repository.ContractRepository;
import br.com.matteusmoreno.domain.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;

    public PaymentService(PaymentRepository paymentRepository, ContractRepository contractRepository) {
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
    }

    public Payment createPayment(CreatePaymentRequestDto request) {
        Contract contract = contractRepository.findContractById(request.contractId());

        Payment payment = Payment.builder()
                .contractId(request.contractId())
                .type(request.type())
                .amount(request.amount())
                .dueDate(request.dueDate())
                .status(PaymentStatus.PENDING)
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        paymentRepository.persist(payment);

        contract.getPaymentIds().add(payment.getPaymentId());
        contractRepository.update(contract);

        log.info("Payment created with ID: {} for contract: {}", payment.getPaymentId(), request.contractId());
        return payment;
    }

    public Payment findPaymentById(String paymentId) {
        log.info("Finding payment with ID: {}", paymentId);
        return paymentRepository.findPaymentById(paymentId);
    }

    public List<Payment> findPaymentsByContractId(String contractId) {
        log.info("Finding payments for contract: {}", contractId);
        return paymentRepository.findPaymentsByContractId(contractId);
    }

    public Payment registerPayment(RegisterPaymentRequestDto request) {
        Payment payment = paymentRepository.findPaymentById(request.paymentId());

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidDate(java.time.LocalDate.now());
        payment.setMethod(request.method());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.update(payment);

        Contract contract = contractRepository.findContractById(payment.getContractId());
        contract.setTotalAmount(contract.getTotalAmount().add(payment.getAmount()));
        contract.setUpdatedAt(LocalDateTime.now());
        contractRepository.update(contract);

        log.info("Payment {} registered as PAID", request.paymentId());
        return payment;
    }

    public Payment deletePayment(String paymentId) {
        Payment payment = paymentRepository.findPaymentById(paymentId);

        Contract contract = contractRepository.findContractById(payment.getContractId());
        contract.getPaymentIds().remove(payment.getPaymentId());
        contractRepository.update(contract);

        paymentRepository.delete(payment);

        log.info("Payment {} deleted from contract {}", paymentId, payment.getContractId());
        return payment;
    }
}

