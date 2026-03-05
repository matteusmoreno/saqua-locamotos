package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.entity.Expense;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.entity.Payment;
import br.com.matteusmoreno.domain.model.Financial;
import br.com.matteusmoreno.domain.repository.MotorcycleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;

@ApplicationScoped
@Slf4j
public class FinancialService {

    private final MotorcycleRepository motorcycleRepository;

    public FinancialService(MotorcycleRepository motorcycleRepository) {
        this.motorcycleRepository = motorcycleRepository;
    }

    public void saveExpense(Motorcycle motorcycle, Expense expense) {
        log.info("Saving expense for motorcycle {}: {}", motorcycle.getMotorcycleId(), expense);
        this.ensureFinancialInitialized(motorcycle);

        motorcycle.getFinancial().getExpenses().add(expense);
        this.calculateTotal(motorcycle);

        this.motorcycleRepository.update(motorcycle);
        log.info("Expense saved and total recalculated for motorcycle {}: new total is {}", motorcycle.getMotorcycleId(), motorcycle.getFinancial().getTotal());
    }

    public void saveEarning(Motorcycle motorcycle, Payment earning) {
        log.info("Saving earning for motorcycle {}: {}", motorcycle.getMotorcycleId(), earning);
        this.ensureFinancialInitialized(motorcycle);

        motorcycle.getFinancial().getEarnings().add(earning);
        this.calculateTotal(motorcycle);

        this.motorcycleRepository.update(motorcycle);
        log.info("Earning saved and total recalculated for motorcycle {}: new total is {}", motorcycle.getMotorcycleId(), motorcycle.getFinancial().getTotal());
    }

    public void removeEarning(Motorcycle motorcycle, String paymentId) {
        log.info("Removing earning {} from motorcycle {}", paymentId, motorcycle.getMotorcycleId());
        this.ensureFinancialInitialized(motorcycle);

        motorcycle.getFinancial().getEarnings().removeIf(p -> p.getPaymentId().equals(paymentId));
        this.calculateTotal(motorcycle);

        this.motorcycleRepository.update(motorcycle);
        log.info("Earning removed and total recalculated for motorcycle {}: new total is {}", motorcycle.getMotorcycleId(), motorcycle.getFinancial().getTotal());
    }

    public void removeExpense(Motorcycle motorcycle, String expenseId) {
        log.info("Removing expense {} from motorcycle {}", expenseId, motorcycle.getMotorcycleId());
        this.ensureFinancialInitialized(motorcycle);

        motorcycle.getFinancial().getExpenses().removeIf(e -> e.getExpenseId().equals(expenseId));
        this.calculateTotal(motorcycle);

        this.motorcycleRepository.update(motorcycle);
        log.info("Expense removed and total recalculated for motorcycle {}: new total is {}", motorcycle.getMotorcycleId(), motorcycle.getFinancial().getTotal());
    }

    public void updateEarning(Motorcycle motorcycle, Payment updatedPayment) {
        log.info("Updating earning {} in motorcycle {}", updatedPayment.getPaymentId(), motorcycle.getMotorcycleId());
        this.ensureFinancialInitialized(motorcycle);

        motorcycle.getFinancial().getEarnings().replaceAll(p ->
                p.getPaymentId().equals(updatedPayment.getPaymentId()) ? updatedPayment : p);
        this.calculateTotal(motorcycle);

        this.motorcycleRepository.update(motorcycle);
        log.info("Earning updated and total recalculated for motorcycle {}: new total is {}", motorcycle.getMotorcycleId(), motorcycle.getFinancial().getTotal());
    }

    public void updateExpense(Motorcycle motorcycle, Expense updatedExpense) {
        log.info("Updating expense {} in motorcycle {}", updatedExpense.getExpenseId(), motorcycle.getMotorcycleId());
        this.ensureFinancialInitialized(motorcycle);

        motorcycle.getFinancial().getExpenses().replaceAll(e ->
                e.getExpenseId().equals(updatedExpense.getExpenseId()) ? updatedExpense : e);
        this.calculateTotal(motorcycle);

        this.motorcycleRepository.update(motorcycle);
        log.info("Expense updated and total recalculated for motorcycle {}: new total is {}", motorcycle.getMotorcycleId(), motorcycle.getFinancial().getTotal());
    }

    protected void calculateTotal(Motorcycle motorcycle) {
        log.info("Calculating total for motorcycle {}: current financial data is {}", motorcycle.getMotorcycleId(), motorcycle.getFinancial());

        BigDecimal totalEarnings = motorcycle.getFinancial().getEarnings().stream()
                .filter(p -> PaymentStatus.PAID.equals(p.getStatus()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = motorcycle.getFinancial().getExpenses().stream()
                .filter(e -> PaymentStatus.PAID.equals(e.getStatus()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = totalEarnings.subtract(totalExpenses);
        motorcycle.getFinancial().setTotal(total);

        log.info("Total calculated for motorcycle {}: total earnings (paid) = {}, total expenses (paid) = {}, final total = {}",
                motorcycle.getMotorcycleId(), totalEarnings, totalExpenses, total);
    }

    protected void ensureFinancialInitialized(Motorcycle motorcycle) {
        if (motorcycle.getFinancial() == null) {
            log.warn("Motorcycle {} has no Financial object, initializing it now", motorcycle.getMotorcycleId());
            motorcycle.setFinancial(Financial.builder().build());
        }
        if (motorcycle.getFinancial().getEarnings() == null) {
            motorcycle.getFinancial().setEarnings(new ArrayList<>());
        }
        if (motorcycle.getFinancial().getExpenses() == null) {
            motorcycle.getFinancial().setExpenses(new ArrayList<>());
        }
    }
}
