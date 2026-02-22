package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.CreatePaymentRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterPaymentRequestDto;
import br.com.matteusmoreno.domain.entity.Payment;
import br.com.matteusmoreno.domain.service.PaymentService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Payment createPayment(CreatePaymentRequestDto request) {
        return paymentService.createPayment(request);
    }

    public Payment findPaymentById(String paymentId) {
        return paymentService.findPaymentById(paymentId);
    }

    public List<Payment> findPaymentsByContractId(String contractId) {
        return paymentService.findPaymentsByContractId(contractId);
    }

    public Payment registerPayment(RegisterPaymentRequestDto request) {
        return paymentService.registerPayment(request);
    }

    public Payment deletePayment(String paymentId) {
        return paymentService.deletePayment(paymentId);
    }
}

